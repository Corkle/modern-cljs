(ns modern-cljs.remotes
  (:require [shoreleave.middleware.rpc :refer [defremote]]
            [modern-cljs.login.validators :as v]))

(defremote calculate [quantity price tax discount]
  (println "calculating...")
  (-> (* (read-string quantity) (read-string price))
      (* (+ 1 (/ (read-string tax) 100)))
      (- (read-string discount))))

(defremote email-domain-errors [email]
  (v/email-domain-errors email))

