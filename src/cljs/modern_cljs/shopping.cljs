(ns modern-cljs.shopping
  (:require [domina.core :refer [append! by-class destroy! by-id value set-value!]]
            [domina.events :refer [listen! prevent-default]]
            [hiccups.runtime]
            [shoreleave.remotes.http-rpc :refer [remote-callback]])
  (:require-macros [hiccups.core :refer [html]]
                   [shoreleave.remotes.macros :as macros]))

(defn calculate [e]
  (let [quantity (value (by-id "quantity"))
        price (value (by-id "price"))
        tax (value (by-id "tax"))
        discount (value (by-id "discount"))]
    (remote-callback :calculate
                     [quantity price tax discount]
                     #(set-value! (by-id "total") (.toFixed % 2)))
    (prevent-default e)))

(defn ^:export init []
  (when (and js/document
           (aget js/document "getElementById"))
    (listen! (by-id "calc") :click #(calculate %))
    (listen! (by-id "calc") :mouseover #(append! (by-id "shoppingForm") (html [:div.help "Click to calculate"])))
    (listen! (by-id "calc") :mouseout #(destroy! (by-class "help")))))
