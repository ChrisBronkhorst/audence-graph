(ns ar.algo
  [:require [ar.graph :refer [neighbours]]]
  (:import (clojure.lang PersistentQueue)))

(defn traverse-graph-dfs [graph s]
  (loop [vertices [] explored #{s} frontier [s]]
    (if (empty? frontier)
      vertices
      (let [v         (peek frontier)
            neighbors (keys (neighbours graph v))]
        (recur
          (conj vertices v)
          (into explored neighbors)
          (into (pop frontier) (remove explored neighbors)))))))

(defn seq-graph-dfs [graph s]
  (letfn [(rec-dfs [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v         (peek frontier)
                      neighbors (keys (neighbours graph v))]
                  (cons v (rec-dfs
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
    (rec-dfs #{s} [s])))

(defn seq-graph-bfs [graph s]
  (letfn [(rec-bfs [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v         (peek frontier)
                      neighbors (keys (neighbours graph v))]
                  (cons v (rec-bfs
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
    (rec-bfs #{s} (conj PersistentQueue/EMPTY s))))

; The author then simplifies it by recognising that only the initial data
; structure for holding the nodes traversed is different between the depth
; and breadth first implementations.

; He then abstacts that out and the result is:
(defn seq-graph [d graph s]
  (letfn [(rec-seq [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v         (peek frontier)
                      neighbors (keys (neighbours graph v))]
                  (cons v (rec-seq
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
    (rec-seq #{s} (conj d s))))

(def seq-graph-dfs (partial seq-graph []))
(def seq-graph-bfs (partial seq-graph PersistentQueue/EMPTY))