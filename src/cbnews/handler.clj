(ns cbnews.handler
  (:require [compojure.core :refer [defroutes]]
            [cbnews.routes.home :refer [home-routes]]
            [cbnews.middleware :as middleware]
            [noir.util.middleware :refer [app-handler]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cbnews.routes.cljsexample :refer [cljs-routes]]
            [cbnews.crawler :as crawler]))

(defroutes
  app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :debug,
     :enabled? true,
     :async? true,
     :max-message-per-msecs nil,
     :fn rotor/appender-fn})
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "cbnews.log", :max-size (* 16 1024 1024), :backlog 50})
  (if (env :dev) (parser/cache-off!))

  (future (crawler/run-me))
  (timbre/info "cbnews started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "cbnews is shutting down..."))

(def app
 (app-handler
   [cljs-routes home-routes app-routes]
   :middleware
   [middleware/template-error-page middleware/log-request]
   :access-rules
   []
   :formats
   [:json-kw :edn]))

