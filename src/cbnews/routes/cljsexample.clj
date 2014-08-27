(ns cbnews.routes.cljsexample
  (:require [compojure.core :refer :all]
            [cbnews.views.layout :as layout]

            [cbnews.crawler :as crawler]))

(def messages
  (atom 
    [{:message "Hello world"
      :user    "Foo"}
     {:message "Ajax is fun"
      :user    "Bar"}]))

(defroutes cljs-routes
  (GET "/cljsexample" [] (layout/render "cljsexample.html"))
  (GET "/messages" [] {:body @messages})
  (POST "/add-message" [message user]
        {:body (swap! messages conj {:message message :user user})})

  (GET "/all-news" []
       {:body @crawler/news-list})
;;  (GET "/all-count" []
;;       {:body @crawler/news-count-list})
)
