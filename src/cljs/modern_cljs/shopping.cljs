(ns modern-cljs.shopping
  (:require [domina.core :refer [append! by-class destroy! by-id value set-value!]]
            [domina.events :refer [listen!]]
            [hiccups.runtime]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [cljs.reader :refer [read-string]])
  (:require-macros [hiccups.core :refer [html]]
                   [shoreleave.remotes.macros :as macros]))

(defn calculate []
  (let [quantity (read-string (value (by-id "quantity")))
        price (read-string (value (by-id "price")))
        tax (read-string (value (by-id "tax")))
        discount (read-string (value (by-id "discount")))]
    (remote-callback :calculate
                     [quantity price tax discount]
                     #(set-value! (by-id "total") (.toFixed % 2)))))

(defn ^:export init []
  (when (and js/document
           (aget js/document "getElementById"))
    (listen! (by-id "calc") :click calculate)
    (listen! (by-id "calc") :mouseover #(append! (by-id "shoppingForm") (html [:div.help "Click to calculate"])))
    (listen! (by-id "calc") :mouseout #(destroy! (by-class "help")))))
