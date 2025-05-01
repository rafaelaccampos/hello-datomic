(ns hello-datomic.modeling)

;;We can't use the two tables we had before.
;;Datomic has a Universal Schema - think of it as one big table with five columns that stores everything in our Database.

;;We don't need to worry about all the five columns right now, focusing on three will be enough:
;Entity identifies the "thing" we are referring to
;Attribute associates an Attribute with the Entity
;Value defines the Value of the Attribute associated with the Entity

(ns hello-datomic.modeling
  (:require [datomic.api :as d]
            [hello-datomic.setup :refer [hello-datomic-conn]]))

(def conn (hello-datomic-conn))

;;To install Attributes we need to define at least three things for each of them:
;:db/ident an identifier, like :person/first-name
;:db/valueType a type for the values of this attribute, like :db.type/string or others
;:db/cardinality whether this attribute accepts one or many values

(defn install-attributes [conn]
  @(d/transact conn [
                     {:db/ident :person/first-name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A person's name"
                      :db/unique :db.unique/identity}

                     {:db/ident :person/last-name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A person's last name"
                      :db/unique :db.unique/identity}

                     {:db/ident :likes/drink
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A favorite drink"}

                     {:db/ident :likes/food
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A favorite food"}
                     ]))

(println (install-attributes conn))

;;Now that our attributes are installed, let's add our data.
(defn load-data [conn]
  @(d/transact conn [
                     {:person/first-name "Helena"
                      :person/last-name "Almeida"
                      :likes/food "pizza"
                      :likes/drink "beer"}

                     {:person/first-name "Alice"
                      :person/last-name "Campos"
                      :likes/food "sushi"
                      :likes/drink "wine"}

                     {:person/first-name "Laura"  :person/last-name "Ferreira" :likes/food "pizza" :likes/drink "water"}
                     {:person/first-name "Miguel" :person/last-name "Melo"     :likes/food "pizza" :likes/drink "water"}
                     {:person/first-name "Arthur" :person/last-name "Ramos"    :likes/food "tacos" :likes/drink "beer"}
                     {:person/first-name "Noah"   :person/last-name "Silva"    :likes/food "curry" :likes/drink "beer"}
                     ])
  )

(load-data conn)

(d/q '[:find ?n
       :where [?e :likes/food "pizza"]
       [?e :person/first-name ?n]]
     (d/db conn))
; #{["Laura"] ["Helena"] ["Miguel"]}



