(ns ar.generate
  [:require [ar.graph :refer [add-edge
                              remove-edge
                              empty-graph
                              count-edges]]])

; ================== Question 2 ====================
; Generate a random graph
; Input:
; N - size of generated graph (number of nodes)
; S - sparseness (number of directed edges actually; from N-1 (inclusive) to N(N-1) (inclusive))
; Output
;  - simple connected graph G(n,s) with N vertices and S edges)

(defn random-connection-proposals
  "Generate a lazy sequence of random pairs from the given nodes."
  [nodes]
  (let [nodes (vec nodes)]
    (letfn [(rec-random-pairs []
              (lazy-seq
                (loop [from (rand-nth nodes)
                       to   (rand-nth nodes)]
                  (if (not= from to) ; no self loops
                    (cons [from to] (rec-random-pairs))
                    (recur (rand-nth nodes) (rand-nth nodes))))))]
      (rec-random-pairs))))

(comment
  (count (take 5 (random-connection-proposals (set (range 1 200)))))

  (every? (fn [[from to]] (not= from to))
          (take 1000 (random-connection-proposals (set (range 1 200)))))

  nil)

(defn random-weight []
  (inc (rand-int 100)))

(defn generate-graph [n s]
  "Total possible connections without self loops is n^2-n"
  (assert (<= s (* n (dec n))) "Too many edges")
  (let [graph (-> (empty-graph)
                  (assoc :nodes (set (range 1 (inc n)))))]
    (loop [i              0
           g              graph
           possible-edges (random-connection-proposals (:nodes graph))]
      (if (= i s)
        g
        (let [[from to] (first possible-edges)]
          (if (get-in g [:out from to])
            ; edge already exists
            (recur i g (rest possible-edges))
            (recur (inc i) (add-edge g from to (random-weight)) (rest possible-edges))))))))

(comment
  (count-edges (generate-graph 10 90)))