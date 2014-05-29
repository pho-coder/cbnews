(ns cbnews.test.util
  (:use clojure.test
        cbnews.util))

(deftest test-crawl-cnbeta
  (testing "testing crawl cnbeta"
    (let [realtime-info (crawl-cnbeta)]
      (println realtime-info)
      (is (not (empty? realtime-info)))
      (is (= (count realtime-info) 12)))))
