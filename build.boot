(set-env!
 :source-paths #{"src/cljc" "src/clj" "src/cljs"}
 :resource-paths #{"html"}

 :dependencies '[
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [adzerk/boot-cljs "1.7.170-3"]
                 [pandeiro/boot-http "0.7.0"]
                 [adzerk/boot-reload "0.4.2"]
                 [adzerk/boot-cljs-repl "0.3.0"]
                 [adzerk/boot-test "1.0.7"]
                 [com.cemerick/piggieback "0.2.1"]
                 [weasel "0.7.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojars.magomimmo/domina "2.0.0-SNAPSHOT"]
                 [hiccups "0.3.0"]
                 [compojure "1.4.0"]
                 [org.clojars.magomimmo/shoreleave-remote-ring "0.3.1"]
                 [org.clojars.magomimmo/shoreleave-remote "0.3.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojars.magomimmo/valip "0.4.0-SNAPSHOT"]
                 [enlive "1.1.6"]
                 [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT"]
                 ])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-test :refer [test]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(def defaults {:test-dirs #{"test/cljc" "test/clj" "test/cljs"}
               :output-to "main.js"
               :testbed :phantom
               :namespaces '#{modern-cljs.shopping.validators-test
                              modern-cljs.login.validators-test}})

(deftask add-source-paths
  "Add paths to :source-paths environment variable"
  [t dirs PATH #{str} ":source-paths"]
  (merge-env! :source-paths dirs)
  identity)

(deftask tdd
  "Launch a TDD Environment"
  [e testbed        ENGINE kw     "The JS testbed engine (default phantom)"
   k httpkit               bool   "Use http-kit web server (default jetty)"
   n namespaces     NS     #{sym} "the set of namespace symbols to run tests in"
   o output-to      NAME   str    "The JS output file name for test (default main.js)"
   O optimizations  LEVEL  kw     "The optimization level (default none)"
   p port           PORT   int    "The web server port to listen on (default 3000)"
   t dirs           PATH   #{str} "Test paths (default test/clj test/cljs test/cljc)"
   v verbose               bool   "Print which files have changed (default false)"]
  (let [dirs (or dirs (:test-dirs defaults))
        output-to (or output-to (:output-to defaults))
        testbed (or testbed (:testbed defaults))
        namespaces (or namespaces (:namespaces defaults))]
    (comp
      (serve :handler 'modern-cljs.core/app
             :resource-root "target"
             :reload true
             :httpkit httpkit
             :port port)
      (add-source-paths :dirs dirs)
      (watch :verbose verbose)
      (reload)
      (cljs-repl)
      (cljs)
;;      (test-cljs :out-file output-to
;;                 :js-env testbed
;;                 :update-fs? true
;;                 :namespaces namespaces
;;                 :optimizations optimizations)
      (test :namespaces namespaces)
      (target :dir #{"target"}))))

(deftask dev
    "Launch Immediate Feedback Development Environment"
    []
    (comp
        (serve :handler 'modern-cljs.core/app
               :resource-root "target"
               :reload true)
        (watch)
        (reload)
        (cljs-repl)
        (cljs)
        (target :dir #{"target"})))
