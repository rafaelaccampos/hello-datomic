(ns hello-datomic.changes
  (:require [datomic.api :as d]
            [clojure.pprint :as pp]
            [hello-datomic.setup :refer [hello-datomic-conn]]
            [hello-datomic.modeling :refer [install-attributes load-data]]))

(def conn (hello-datomic-conn))
(install-attributes conn)
(load-data conn)

;;To make sure everything is working as expected, run a query to list what everyone likes
(d/q '[:find ?n ?food ?drink
       :where
       [?e :person/first-name ?n]
       [?e :likes/food ?food]
       [?e :likes/drink ?drink]]
     (d/db conn))
;;#{["Alice" "sushi" "wine"] ["Helena" "pizza" "beer"] ["Miguel" "pizza" "water"] ["Noah" "curry" "beer"] ["Arthur" "tacos" "beer"] ["Laura" "pizza" "water"]}

;;Let's create a new friend, we still don't know their favorite food or drink.
@(d/transact conn [{:person/first-name "Pedro" :person/last-name "Prado"}])

;;list everyone that has no favorite food
(d/q '[:find ?fn ?ln
       :where
       [?e :person/first-name ?fn]
       [?e :person/last-name ?ln]
       [(missing? $ ?e :likes/food)]]
     (d/db conn))
;;#{["Pedro" "Prado"]}

;;let's update Noah's :likes/food to "salad"
@(d/transact conn [{:db/id [:person/first-name "Noah"],
                  :likes/food "salad"}])

;;query the history to get historical :likes/food values for Noah
(pp/pprint
  (sort-by first
           (d/q '[:find ?txI ?likes ?op
                  :where
                  [?e :person/first-name "Noah"]
                  [?e :likes/food ?likes ?tx ?op]
                  [?tx :db/txInstant ?txI]]
                (d/history (d/db conn)))))