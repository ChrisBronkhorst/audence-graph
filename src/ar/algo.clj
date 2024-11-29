(ns ar.algo
  [:require [ar.graph :refer [neighbours]]
            [clojure.data.priority-map :as pm]]
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

; He then abstracts that out and the result is:
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

(def INF Long/MAX_VALUE)

(defn trace-path [parents from to]
  (loop [current to
         path    [to]]
    (if (= current from)
      (reverse path)
      (if-let [parent (parents current)]
        (recur parent (conj path parent))
        nil))))

(defn full-dijkstra [graph from to]
  (assert (and (contains? (:nodes graph) from)
               (contains? (:nodes graph) to))
          "Nodes not in graph")
  (let [nodes      (:nodes graph)
        !parents   (atom {})                                ; ugh. not happy about this.
        path-costs (loop [frontier (reduce (fn [pm node] (assoc pm node INF))
                                           (pm/priority-map from 0)
                                           (disj nodes from))
                          visited  {}]
                     (let [[current current-cost] (first frontier)
                           update-cost (fn [costs [node cost]] (if (< cost (costs node))
                                                                 (do (swap! !parents #(assoc % node current))
                                                                     (assoc costs node cost))
                                                                 costs))]
                       (if (or (or (not current) (= current-cost INF))
                               (= current to)) ; early stopping
                         visited
                         (let [neighbours (neighbours graph current)
                               edge-costs (->> neighbours
                                               ; only consider neighbours still in frontier
                                               (filter (comp frontier first))
                                               ; calculate the edge cost of the neighbours through the current node
                                               (map (fn [[node edge-cost]] [node (+ current-cost edge-cost)])))
                               new-costs  (reduce update-cost frontier edge-costs)]
                           (recur (dissoc new-costs current)
                                  (assoc visited current current-cost))))))]
    {:parents @!parents
     :costs   path-costs}))


(defn dijkstra [graph from to]
  (let [{:keys [parents]} (full-dijkstra graph from to)]
    (trace-path parents from to)))

(comment
  (= Long/MAX_VALUE))
