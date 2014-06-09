(ns cbnews.test.util
  (:use clojure.test
        cbnews.util))

(deftest test-crawl-cnbeta
  (testing "testing crawl cnbeta"
    (let [raw-html-content (http-get "http://www.cnbeta.com")
          realtime-info (parse-cnbeta raw-html-content)]
      (println raw-html-content)
      (println realtime-info)
      (is (not (empty? realtime-info)))
      (is (= (count realtime-info) 12)))))
