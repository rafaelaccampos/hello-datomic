(ns hello-datomic.setup
  (:require [datomic.api :as d]))

;;Note that d/create-database will return false if you run it more than once.
;;We can't create something that already exists. To make sure we always have a fresh database, let's call d/delete-database before creating the database.

(defn hello-datomic-conn
  "Create a connection to an anonymous, in-memory database."
  []
  (let [uri "datomic:mem://hello-datomic"]
    (d/delete-database uri)
    (d/create-database uri)
    (d/connect uri)))

(def conn (hello-datomic-conn))
(println conn)
; #object[datomic.peer.LocalConnection 0x26580f51 datomic.peer.LocalConnection@26580f51]

;;Use transact to install a new attribute called :person/first-name
@(d/transact conn [{:db/ident :person/first-name
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "A person's name"
                    :db/unique :db.unique/identity}])

;;Use transact again, now creating entities with different values for :person/first-name
@(d/transact conn [
  {:person/first-name "Helena"}
  {:person/first-name "Alice"}
  {:person/first-name "Laura"}
  {:person/first-name "Miguel"}
  {:person/first-name "Arthur"}
  {:person/first-name "Noah"}
])

;;Now that our database has some data. We can query it using
(d/q '[:find ?n
       :where [?e :person/first-name ?n]]
     (d/db conn))
;;#{["Noah"] ["Laura"] ["Arthur"] ["Helena"] ["Alice"] ["Miguel"]}