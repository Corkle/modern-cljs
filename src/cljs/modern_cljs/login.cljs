(ns modern-cljs.login
  (:require [domina.core :refer [by-id value append! by-class destroy! prepend! attr]]
            [domina.events :refer [listen! prevent-default stop-propagation]]
            [hiccups.runtime]
            [modern-cljs.login.validators :refer [user-credential-errors]]
            [shoreleave.remotes.http-rpc :refer [remote-callback]])
  (:require-macros [hiccups.core :refer [html]]
                   [shoreleave.remotes.macros :as shore-macros]))

(defn validate-email-domain [email]
  (remote-callback :email-domain-errors
                   [email]
                   #(if %
                      (do
                        (prepend! (by-id "loginForm")
                                  (html [:div.help.email "The email domain doesn't exist."]))
                        false)
                      true)))

(defn validate-email [email]
  (destroy! (by-class "email"))
  (if-let [errors (:email (user-credential-errors (value email) nil))]
    (do
      (prepend! (by-id "loginForm") (html [:div.help.email (first errors)]))
      false)
    (validate-email-domain (value email))))

(defn validate-password [password]
  (destroy! (by-class "password"))
  (if-let [errors (:email (user-credential-errors nil (value password)))]
    (do
      (append! (by-id "loginForm") (html [:div.help.password (first errors)]))
      false)
    true))

(defn validate-form [e email password]
  (if-let [{e-errs :email p-errs :password} (user-credential-errors (value email) (value password))]
    (if (or e-errs p-errs)
      (do
        (destroy! (by-class "help"))
        (prevent-default e)
        (append! (by-id "loginForm") (html [:div.help "Please complete the form"])))
      true)))

(defn ^:export init []
  (if (and js/document
           (aget js/document "getElementById"))
    (let [email (by-id "email")
          password (by-id "password")]
      (listen! (by-id "loginForm") :submit #(validate-form % email password))
      (listen! email :blur #(validate-email email))
      (listen! password :blur #(validate-password password)))))
