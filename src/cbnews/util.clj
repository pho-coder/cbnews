(ns cbnews.util
  (:require [noir.io :as io]
            [markdown.core :as md]
            [hickory.core :refer [parse as-hickory]]
            [hickory.zip :refer [hickory-zip]]
            [hickory.select :as h-select]
            [clj-http.client :as http-client])
  (:import [java.util UUID]
           [java.net URL URLConnection]
           [java.io InputStreamReader BufferedReader]))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))

(defn http-get [url]
  (try
    (let [conn (.openConnection ^URL (URL. url))]
      #_(.setRequestProperty conn "accept" "*/*")
      #_(.setRequestProperty conn "connection" "Keep-Alive")
      #_(.setRequestProperty conn "user-agent" "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
      (.setUseCaches ^URLConnection conn false)
      (.connect ^URLConnection conn)
      (let [in (BufferedReader. (InputStreamReader. (.getInputStream conn)))]
        (loop [html "" line (.readLine in)]
          (if (= line nil)
            html
            (recur (str html "\n" line) (.readLine in))))))
    (catch Exception e
      (.printStackTrace e))))

(defn parse-cnbeta
  [raw-html-content]
  (let [html-content (-> raw-html-content parse as-hickory)
        realtime-content (h-select/select (h-select/descendant (h-select/class "realtime_list")
                                                               (h-select/tag :li))
                                          html-content)
        extract-info (fn [realtime-li]
                       {:sid (:data-sid (:attrs realtime-li))
                        :href (:href (:attrs (first (:content (nth (:content realtime-li) 3)))))
                        :title (first (:content (first (:content (nth (:content realtime-li) 3)))))
                        :image (:original (:attrs (first (:content (second (:content (second (:content (nth (:content realtime-li) 5)))))))))
                        :brief (first (:content (nth (:content (nth (:content (second (:content (nth (:content realtime-li) 5)))) 3)) 3)))
                        :time (apply str (butlast (next (first (:content (first (:content (nth (:content (nth (:content (second (:content (nth (:content realtime-li) 5)))) 3)) 4))))))))})]
    #_(prn realtime-content)
    (map #(extract-info %) realtime-content)
    #_(doseq [realtime-li realtime-content]
        (prn (extract-info realtime-li)))))

(defn exception-cause? [klass ^Throwable t]
  (->> (iterate #(.getCause ^Throwable %) t)
       (take-while identity)
       (some (partial instance? klass))
       boolean))

(defn current-time-millis []
  (System/currentTimeMillis))

(defn current-time []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd hh:mm:ss") (java.util.Date.)))

(defn uuid []
  (str (UUID/randomUUID)))

(defn time2timestamp [time-str]
  (try
    (.getTime (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") time-str))
    (catch Exception e
      0)))
