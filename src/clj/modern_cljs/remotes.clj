(ns modern-cljs.remotes
  (:require [modern-cljs.core :refer [handler]]
            [compojure.handler :refer [site]]
            [shoreleave.middleware.rpc :refer [defremote wrap-rpc]]
            [modern-cljs.login.validators :as v]))

(defremote calculate [quantity price tax discount]
  (println "calculating...")
  (-> (* quantity price)
      (* (+ 1 (/ tax 100)))
      (- discount)))

(defremote email-domain-errors [email]
  (v/email-domain-errors email))

(def app (-> (var handler)
             (wrap-rpc)
             (site)))
