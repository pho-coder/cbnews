(ns cbnews.crawler
  (:require [taoensso.timbre :as timbre]

            [cbnews.util :as util]
            [cbnews.timer :as timer]))

(def news-list (atom (list)))

(def news-count-list (atom []))

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
        #_(timbre/debug crawl-result)
        #_(timbre/debug news-list-lastest)
        #_(timbre/debug "exists?" news-exists?)
        (reset! news-count-list (conj @news-count-list (count crawl-result)))
        (if news-exists?
          (let [index (loop [i 0]
                        (cond
                         (>= i (count crawl-result)) nil ;this case can't come out
                         (= (:sid news-list-lastest) (:sid (nth crawl-result i))) i
                         :else (recur (inc i))))]
            #_(timbre/debug "index: " index)
            #_(timbre/debug "add me: " (nthnext (reverse crawl-result) (- (count crawl-result) index)))
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
  (let [timer (timer/mk-timer)]
    (timer/schedule-recurring timer 5 10 (fn []
                                           (crawling)
                                           (throw-old-news 50)))
    (while true
      (timbre/info @news-list)
      (timbre/info (count @news-list))
      (Thread/sleep 30000))))
