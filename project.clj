(defproject
  cbnews
  "0.1.0-SNAPSHOT"
  :repl-options
  {:init-ns cbnews.repl}
  :dependencies
  [[ring-server "0.3.1"]
   [domina "1.0.2"]
   [environ "0.5.0"]
   [markdown-clj "0.9.43"]
   [com.taoensso/timbre "3.1.6"]
   [prismatic/dommy "0.1.2"]
   [org.clojure/clojurescript "0.0-2138"]
   [org.clojure/clojure "1.6.0"]
   [com.taoensso/tower "2.0.2"]
   [cljs-ajax "0.2.3"]
   [compojure "1.1.6"]
   [selmer "0.6.6"]
   [lib-noir "0.8.2"]
   [hickory "0.5.3"]
   [clj-http "0.9.2"]]
  :cljsbuild
  {:builds
   [{:source-paths ["src-cljs"],
     :compiler
     {:pretty-print false,
      :output-to "resources/public/js/site.js",
      :optimizations :advanced}}]}
  :ring
  {:handler cbnews.handler/app,
   :init cbnews.handler/init,
   :destroy cbnews.handler/destroy,
   :servlet-path-info? false}
  :profiles
  {:uberjar {:aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? true}},
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.2"]],
    :env {:dev true}}}
  :url
  "http://example.com/FIXME"
  :plugins
  [[lein-ring "0.8.10"]
   [lein-environ "0.5.0"]
   [lein-cljsbuild "0.3.3"]]
  :description
  "FIXME: write description"
  :min-lein-version "2.0.0")
