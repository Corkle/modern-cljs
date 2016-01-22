(ns modern-cljs.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found files resources]]
            [compojure.handler :refer [site]]
            [modern-cljs.login :refer [authenticate-user]]
            [modern-cljs.templates.shopping :refer [shopping]]
            [shoreleave.middleware.rpc :refer [wrap-rpc]]))

(defroutes handler
  (GET "/" [] "Hello from Compojure!")
  (POST "/login" [email password] (authenticate-user email password))
  (POST "/shopping" [quantity price tax discount] (shopping quantity price tax discount))
  (files "/" {:root "target"})
  (resources "/" {:root "target"})
  (not-found "Page Not Found"))

(def app (-> (var handler)
             (wrap-rpc)
             (site)))
