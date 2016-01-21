(ns modern-cljs.login
  (:require [domina.core :refer [by-id value set-value!]]))

(defn validate-form []
  (if (and (> (count (value (by-id "email"))) 0)
           (> (count (value (by-id "password"))) 0))
      true
      (do (js/alert "Please, complete the form!")
          false)))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (let [login-form (.getElementById js/document "loginForm")]
      (set! (.-onsubmit login-form) validate-form))))

(set! (.-onload js/window) init)
