(ns cbnews.test.timer
  (:use clojure.test
        cbnews.util
        cbnews.timer))

(deftest test-timer
  (testing "testing timer"
    (let [timer (mk-timer)]
      (schedule-recurring timer 5 10 (fn [] (println current-time-millis))))))
