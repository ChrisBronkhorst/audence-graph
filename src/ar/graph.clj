(ns ar.graph
  (:import (clojure.lang PersistentQueue)))

; This link has a simple explanation of a simple Graph definition "language"
; and implementations of depth/breadth graph search algorithms.
;
; http://hueypetersen.com/posts/2013/06/25/graph-traversal-with-clojure/
;
; The code is reproduced here for readability:

(defn edge [node weight]
  {:node node :weight weight})

(def G {:1 [(edge :2 1) (edge :3 1)],
        :2 [(edge :4 3)],
        :3 [(edge :4 5)],
        :4 []})

(defn traverse-graph-dfs [graph s]
  (loop [vertices [] explored #{s} frontier [s]]
    (if (empty? frontier)
      vertices
      (let [v         (peek frontier)
            neighbors (map :node (graph v))]
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
                      neighbors (map :node (graph v))]
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
                      neighbors (map :node (graph v))]
                  (cons v (rec-bfs
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
    (rec-bfs #{s} (conj PersistentQueue/EMPTY s))))

(traverse-graph-dfs G :1) ; => [:1 :3 :4 :2]
(seq-graph-dfs G :1) ; => (:1 :3 :4 :2)
(seq-graph-bfs G :1) ; => (:1 :2 :3 :4)

; The author then simplifies it by recognising that only the initial data
; structure for holding the nodes traversed is different between the depth
; and breadth first implementations.

;He then abstacts that out and the result is:

(defn seq-graph [d graph s]
  (letfn [(rec-seq [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v         (peek frontier)
                      neighbors (map :node (graph v))]
                  (cons v (rec-seq
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
   (rec-seq #{s} (conj d s))))

(def seq-graph-dfs (partial seq-graph []))
(def seq-graph-bfs (partial seq-graph PersistentQueue/EMPTY))

(seq-graph-dfs G :1) ; => (:1 :3 :4 :2)
(seq-graph-bfs G :1) ; => (:1 :2 :3 :4)

;==================== QUESTION 1 ====================
;1. Extend the graph definition to include a weight between graph edges

; ================== Question 2 ====================
; Generate a random graph
; Input:
; N - size of generated graph (number of nodes)
; S - sparseness (number of directed edges actually; from N-1 (inclusive) to N(N-1) (inclusive))
;                        Output:
;                        simple connected graph G(n,s) with N vertices and S edges)
(defn generate-graph [n s]
  (let [nodes (map (comp keyword str) (range 1 (inc n)))]
    nodes))

(generate-graph 5 5) ; => (:1 :2 :3 :4 :5)