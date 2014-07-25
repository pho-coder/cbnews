(ns cbnews.crawler
  (:require [taoensso.timbre :as timbre]

            [cbnews.util :as util]
            [cbnews.timer :as timer-util]))

(def news-list (atom (list)))

;;(def news-count-list (atom []))

(def reset-timer-times (atom 0))

(defn crawling []
  (let [raw-html-content (util/http-get "http://www.cnbeta.com")]
    (if (empty? raw-html-content)
      (timbre/warn "http get nil!")
      (let [crawl-result (util/parse-cnbeta raw-html-content)
            news-list-lastest (first @news-list)
            news-exists? (reduce #(or %1
                                      (= (:sid %2) (:sid news-list-lastest)))
                                 false
                                 crawl-result)]
;;        (reset! news-count-list (conj @news-count-list (count crawl-result)))
        (timbre/debug crawl-result)
        (if news-exists?
          (let [index (loop [i 0]
                        (cond
                         (>= i (count crawl-result)) nil ;this case can't come out
                         (= (:sid news-list-lastest) (:sid (nth crawl-result i))) i
                         :else (recur (inc i))))]
            (if (and index
                     (not= index 0)) ;this case means not new
              (reset! news-list (into @news-list (nthnext (reverse crawl-result) (- (count crawl-result) index))))))
          (reset! news-list (into @news-list (reverse crawl-result))))))))

(defn throw-old-news [hours]
  (let [count-news (count @news-list)]
    (if (> count-news 0)
      (let [index (loop [i (dec count-news)]
                    (if (< (- (util/current-time-millis)
                              (util/time2timestamp (:time (nth @news-list i))))
                           (* hours 60 60 1000))
                      (inc i)
                      (if (= i 0)
                        0
                        (recur (dec i)))))]
        (if (< index count-news)
          (reset! news-list (drop-last (- count-news index) @news-list)))))))

(defn run-me
  []
  (while true
;;    (timbre/debug @news-list)
    (timbre/debug (count @news-list))
    (Thread/sleep 30000)
    (crawling)
    (throw-old-news 50)))
