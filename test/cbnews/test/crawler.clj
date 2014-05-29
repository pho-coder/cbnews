(ns cbnews.test.crawler
  (:use clojure.test
        cbnews.crawler))

(deftest test-crawling
  (testing "testing crawling"
    (crawling)))
