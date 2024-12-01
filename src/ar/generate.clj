(ns ar.generate
  [:require [ar.graph :refer [add-edge
                              remove-edge
                              empty-graph
                              count-edges
                              add-nodes]]])
(defn random-weight []
  (inc (rand-int 100)))

; new approach
; keep a set of nodes that are in the "connected set"
; keep another set of nodes that are currently in the unconnected set.
; randomly connect nodes from the unconnected set to nodes in the connected set.
; (I think this might generate a dag not a graph)
; Oh, it will generate a Dag if we have exactly the right amount of edges to connect all the nodes.
; but it will create a graph if we have more edges than that.
; so after there are no nodes left unconnected, then we will start connecting nodes
; that are in the connected set to other nodes in the connected set.

(defn random-deduped-pairs
  "Given a vector of items, generate a lazy sequence of random pairs of items.
   The pairs are not allowed to be the same item."
  [items]
  (letfn [(rec-random-pairs []
            (lazy-seq
              (loop [from (rand-nth items)
                     to   (rand-nth items)]
                (if (not= from to)                          ; no self loops
                  (cons [from to] (rec-random-pairs))
                  (recur (rand-nth items) (rand-nth items))))))]
    (rec-random-pairs)))

(defn make-graph
  "Total possible connections without self loops is n^2-n.
   Minimum possible connections is n-1 to ensure a connected graph"
  [n s]
  (assert (<= s (* n (dec n))) "Too many edges")
  (assert (>= s (dec n)) "Too few edges")
  (let [nodes    (set (range 1 (inc n)))
        graph    (add-nodes (empty-graph) nodes)
        shuffled (shuffle nodes)]
    (loop [i           0
           graph       graph
           unconnected (rest shuffled)                      ; we can leave this as a list because we take from the front
           connected   [(first shuffled)]                   ; this needs to be a vec because we will randomly select from it
           pairs       (random-deduped-pairs shuffled)]     ; we only use this once the unconnected set is empty (we have a connected graph
      (if (= i s)
        graph
        (if (seq unconnected)                               ; we still have unconnected nodes
          (let [from (rand-nth connected)
                to   (first unconnected)]
            (recur (inc i)
                   (add-edge graph from to (random-weight))
                   (rest unconnected)
                   (conj connected to)
                   pairs))

          ; randomly connect nodes now that we have a connected graph
          (let [[from to] (first pairs)]
            (if (get-in graph [:out from to])               ; this connection already exists
              (recur i graph unconnected connected (rest pairs))
              (recur (inc i)
                     (add-edge graph from to (random-weight))
                     unconnected
                     connected
                     (rest pairs)))))))))

(comment
  (make-graph 10 10)
  (make-graph 10 15)
  (make-graph 10 50)
  (count-edges (make-graph 10 90))
  (count-edges (make-graph 10 2))
  (count-edges (make-graph 10 10))
  (count-edges (make-graph 10 9))
  (count-edges (make-graph 10 50))
  (count-edges (make-graph 15 51))
  (count-edges (make-graph 15 52))
  (count-edges (make-graph 4 10))

  ; check that every node has at least one incoming edge
  (let [edges 5
        graph (make-graph 5 edges)]
    (and
      (= (set (range 1 edges))
         (set (keys (:in graph))))
      (count-edges graph)))

  ; the graph is random
  (not=
    (make-graph 10 90)
    (make-graph 10 90))


  nil)