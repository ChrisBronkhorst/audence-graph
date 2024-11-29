(ns ar.generate
  [:require [ar.graph :refer [add-edge
                              remove-edge
                              empty-graph
                              count-edges
                              add-nodes]]])

; ================== Question 2 ====================
; Generate a random graph
; Input:
; N - size of generated graph (number of nodes)
; S - sparseness (number of directed edges actually; from N-1 (inclusive) to N(N-1) (inclusive))
; Output
;  - simple connected graph G(n,s) with N vertices and S edges)

;(defn random-connection-proposals
;  "Generate a lazy sequence of random pairs from the given nodes."
;  [nodes]
;  (let [nodes (vec nodes)]
;    (letfn [(rec-random-pairs []
;              (lazy-seq
;                (loop [from (rand-nth nodes)
;                       to   (rand-nth nodes)]
;                  (if (not= from to) ; no self loops
;                    (cons [from to] (rec-random-pairs))
;                    (recur (rand-nth nodes) (rand-nth nodes))))))]
;      (rec-random-pairs))))
;
;(comment
;  (count (take 5 (random-connection-proposals (set (range 1 200)))))
;
;  (every? (fn [[from to]] (not= from to))
;          (take 1000 (random-connection-proposals (set (range 1 200)))))
;
;  nil)

(defn random-weight []
  (inc (rand-int 100)))

;(defn generate-graph [n s]
;  "Total possible connections without self loops is n^2-n"
;  (assert (<= s (* n (dec n))) "Too many edges")
;  (let [graph (-> (empty-graph)
;                  (assoc :nodes (set (range 1 (inc n)))))]
;    (loop [i              0
;           g              graph
;           possible-edges (random-connection-proposals (:nodes graph))]
;      (if (= i s)
;        g
;        (let [[from to] (first possible-edges)]
;          (if (get-in g [:out from to])
;            ; edge already exists
;            (recur i g (rest possible-edges))
;            (recur (inc i) (add-edge g from to (random-weight)) (rest possible-edges))))))))

; I need to rethink this.  Making sure that we have no partitions in the graph means we cannot locally
; start connecting things without bookkeeping the global state of the graph.

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