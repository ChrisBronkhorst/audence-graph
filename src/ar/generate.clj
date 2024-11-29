(ns ar.generate
  [:require [ar.graph :refer [edge]]])

; ================== Question 2 ====================
; Generate a random graph (spanning tree or disconnected? Can it be counted as a graph if it's disconnected? Or would it be multiple graphs?)
; Input:
; N - size of generated graph (number of nodes)
; S - sparseness (number of directed edges actually; from N-1 (inclusive) to N(N-1) (inclusive))
;                        Output:
;                        simple connected graph G(n,s) with N vertices and S edges)

(defn generate-graph [n s]
  (let [nodes (mapv (comp keyword str) (range 1 (inc n)))
        edges (for [i (range s)]
                (let [node (rand-nth nodes)]
                  node))]

    edges))

(defn random-weight []
  (inc (rand-int 10))) ; Generate weights between 1 and 10

(defn generate-vertex-keys [n]
  (map #(keyword (str %)) (range 1 (inc n))))

(defn ensure-connectivity [n]
  "Creates a spanning tree"
  (let [vertices (generate-vertex-keys n)]
    (into {}
          (map-indexed
            (fn [idx vertex]
              (if (< idx (dec n))
                [vertex [(edge (nth vertices (inc idx)) (random-weight))]]
                [vertex []]))
            vertices))))

(defn add-random-edges [graph n s]
  "Adds additional random edges to reach desired sparseness"
  (let [vertices (keys graph)
        edges    (loop [])]))

(defn make-graph
  "Generate a random connected graph with n vertices and s edges"
  [n s]
  {:pre [(>= s (dec n))
         (<= s (* n (dec n)))]}
  (-> (ensure-connectivity n)
      (add-random-edges n s)))