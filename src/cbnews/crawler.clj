(ns cbnews.crawler
  (:require [taoensso.timbre :as timbre]

            [cbnews.util :as util]
            [cbnews.timer :as timer]))

(def news-list (atom (list)))

(defn crawling
  []
  (let [timer (timer/mk-timer)]
      (timer/schedule-recurring timer 5 60000 (fn []
                                             (let [crawl-result (util/crawl-cnbeta)
                                                   news-list-lastest (first @news-list)
                                                   news-exists? (reduce #(or %1
                                                                             (= (:sid %2) (:sid news-list-lastest)))
                                                                        false
                                                                        crawl-result)]
                                               #_(timbre/debug crawl-result)
                                               #_(timbre/debug news-list-lastest)
                                               #_(timbre/debug "exists?" news-exists?)
                                               (if news-exists?
                                                 (let [index (loop [i 0]
                                                               (cond
                                                                (>= i (count crawl-result)) nil ;this case can't come out
                                                                (= news-list-lastest (:sid (nth crawl-result i))) i
                                                                :else (recur (inc i))))]
                                                   (if (and index
                                                            (not= index 0)) ;this case means not new
                                                     (reset! news-list (into @news-list (nthnext (reverse crawl-result) (- (count crawl-result) 1 index))))))
                                                 (reset! news-list (into @news-list (reverse crawl-result)))))))
      (while true
        (timbre/info @news-list)
        (timbre/info (count @news-list))
        (Thread/sleep 300000))))
