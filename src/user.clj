(ns user
  [:require [ar.algo :as algo]
            [ar.generate :as generate]
            [ar.graph :as graph :refer [empty-graph
                                        add-edge
                                        count-edges
                                        neighbours]]])

; refresh deps without restarting the REPL
; need the new version of the clojure cli tools
#_(clojure.repl.deps/sync-deps)



; This link has a simple explanation of a simple Graph definition "language"
; and implementations of depth/breadth graph search algorithms.
;
; http://hueypetersen.com/posts/2013/06/25/graph-traversal-with-clojure/


(def G1 (-> (empty-graph)
            (add-edge :1 :2 1)
            (add-edge :1 :3 1)
            (add-edge :2 :4 3)
            (add-edge :3 :4 5)))

(def G2 (generate/make-graph 10 15)) ; uses integer node ids

(comment
  (algo/traverse-graph-dfs G1 :1) ; => [:1 :3 :4 :2]
  (algo/seq-graph-dfs G1 :1) ; => (:1 :3 :4 :2)
  (algo/seq-graph-bfs G1 :1) ; => (:1 :2 :3 :4)

  (algo/seq-graph-dfs G1 :1) ; => (:1 :3 :4 :2)
  (algo/seq-graph-bfs G1 :1) ; => (:1 :2 :3 :4)

  (do G2)
  (algo/traverse-graph-dfs G2 2)
  (algo/traverse-graph-dfs G2 1)
  (algo/traverse-graph-dfs G2 5)
  (algo/traverse-graph-dfs G2 6)
  (algo/traverse-graph-dfs G2 8)

  nil)

;==================== QUESTION 1 ====================
;1. Extend the graph definition to include a weight between graph edges


;==================== QUESTION 2 ====================
; generate simple connected graph G(n,s) with N vertices and S edges

; generate and random directed graph (not connected yet)
(comment
  (let [graph (generate/make-graph 10 15)] ; uses integer node ids
    [(count-edges graph)
     (neighbours graph 3)
     (neighbours graph 1)
     (neighbours graph 4)]))

; ==================== QUESTION 3 ====================
; Implement Dijkstra's algorithm to find the shortest path between two nodes

; check that we are doing correctly by creating a square graph
(let [G1 (-> (empty-graph)
             (add-edge :top-left :top-right 1)
             (add-edge :top-left :bottom-left 1)
             (add-edge :top-right :bottom-right 2) ; make this more expensive
             (add-edge :bottom-left :bottom-right 1)
             (add-edge :bottom-right :outside 5))]
  [(time (algo/dijkstra G1 :top-left :bottom-right))
   ; calculate the cost to every reachable node
   (time (algo/dijkstra G1 :top-left :bottom-right false))
   (time (algo/shortest-path G1 :top-left :bottom-right))
   (time (algo/shortest-path G1 :top-left :outside))
   (time (algo/shortest-path G1 :top-right :outside))])

; ==================== QUESTION 4 ====================
; Implement graph distance metrics: eccentricity, radius, diameter

(let [G1 (-> (empty-graph)
             (add-edge :top-left :top-right 1)
             (add-edge :top-left :bottom-left 1)
             (add-edge :top-right :bottom-right 2) ; make this more expensive
             (add-edge :bottom-left :bottom-right 1)
             (add-edge :bottom-right :outside 5))]
  [(algo/eccentricity G1 :top-left)
   (algo/eccentricity G1 :top-right)
   (algo/radius G1)
   (algo/diameter G1)])

; Further thinking, from wikipedia:
; In the case of a directed graph the distance d(u,v)
; between two vertices u and v is defined as the length of
; a shortest directed path from u to v consisting of arcs,
; provided at least one such path exists.[3] Notice that,
; in contrast with the case of undirected graphs, d(u,v) does not necessarily
; coincide with d(v,u)—so it is just a quasi-metric, and it might be the
; case that one is defined while the other is not.

; Note from Chris
; Notice, since eccentricity is a "quasi-metric" on directed graphs,
; We are not always guaranteed to have a radius, diameter of a directed graph.
; Even in the case where the graph is connected, we can always have nodes which
; do not have outgoing connections, even if they are fully reachable from other nodes
; and we do not have any partitions in the graph, as in the example of the square graph above.
; The only node that can reach all of the other nodes is the top-left node.
; If we connect the :outside node to the :top-right node, we will have a radius and diameter.

(let [G1 (-> (empty-graph)
             (add-edge :top-left :top-right 1)
             (add-edge :top-left :bottom-left 1)
             (add-edge :top-right :bottom-right 2) ; make this more expensive
             (add-edge :bottom-left :bottom-right 1)
             (add-edge :bottom-right :outside 5)
             (add-edge :outside :top-left 20))]
  [(algo/eccentricity G1 :top-left)
   (algo/eccentricity G1 :top-right)
   (algo/radius G1)
   (algo/diameter G1)])

; now we see that all of our metrics work, but :top-left has short paths,
; and the other paths need to loop through the :outside node.