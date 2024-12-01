(ns user
  [:require [ar.algo :as algo :refer [eccentricity radius diameter shortest-path]]
            [ar.generate :as generate :refer [make-graph]]
            [ar.graph :as graph :refer [empty-graph
                                        add-edge
                                        count-edges
                                        neighbours]]])

; refresh deps without restarting the REPL
; need the new version of the clojure cli tools
#_(clojure.repl.deps/sync-deps)

;==================== QUESTION 1 ====================
;1. Extend the graph definition to include a weight between graph edges

(comment
  (def G1 (-> (empty-graph)
              (add-edge :1 :2 1)
              (add-edge :1 :3 1)
              (add-edge :2 :4 3)
              (add-edge :3 :4 5)))
  (algo/traverse-graph-dfs G1 :1)                           ; => [:1 :3 :4 :2]
  (algo/seq-graph-dfs G1 :1)                                ; => (:1 :3 :4 :2)
  (algo/seq-graph-bfs G1 :1)                                ; => (:1 :2 :3 :4)

  (algo/seq-graph-dfs G1 :1)                                ; => (:1 :3 :4 :2)
  (algo/seq-graph-bfs G1 :1))                               ; => (:1 :2 :3 :4)

;==================== QUESTION 2 ====================
; generate simple connected graph G(n,s) with N vertices and S edges


(comment
  (def G2 (generate/make-graph 10 15))                      ; uses integer node ids
  (algo/traverse-graph-dfs G2 2)
  (algo/traverse-graph-dfs G2 1)
  (algo/traverse-graph-dfs G2 5)
  (algo/traverse-graph-dfs G2 6)
  (algo/traverse-graph-dfs G2 8)

  nil)

; ==================== QUESTION 3 ====================
; Implement Dijkstra's algorithm to find the shortest path between two nodes

; check that we are doing correctly by creating a square graph
(comment
  (let [G1 (-> (empty-graph)
               (add-edge :top-left :top-right 1)
               (add-edge :top-left :bottom-left 1)
               (add-edge :top-right :bottom-right 2)        ; make this more expensive
               (add-edge :bottom-left :bottom-right 1)
               (add-edge :bottom-right :outside 5))]
    [(algo/dijkstra G1 :top-left :bottom-right)
     ; calculate the cost to every reachable node
     (algo/dijkstra G1 :top-left :bottom-right false)
     (algo/shortest-path G1 :top-left :bottom-right)
     (algo/shortest-path G1 :top-left :outside)
     (algo/shortest-path G1 :top-right :outside)]))

; ==================== QUESTION 4 ====================
; Implement graph distance metrics: eccentricity, radius, diameter

(comment
  (let [G1 (-> (empty-graph)
               (add-edge :top-left :top-right 1)
               (add-edge :top-left :bottom-left 1)
               (add-edge :top-right :bottom-right 2)        ; make this more expensive
               (add-edge :bottom-left :bottom-right 1)
               (add-edge :bottom-right :outside 5))]
    [(algo/eccentricity G1 :top-left)
     (algo/eccentricity G1 :top-right)
     (algo/radius G1)
     (algo/diameter G1)]))

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

(comment
  (let [G1 (-> (empty-graph)
               (add-edge :top-left :top-right 1)
               (add-edge :top-left :bottom-left 1)
               (add-edge :top-right :bottom-right 2)        ; make this more expensive
               (add-edge :bottom-left :bottom-right 1)
               (add-edge :bottom-right :outside 5)
               (add-edge :outside :top-left 20))]
    [(algo/eccentricity G1 :top-left)
     (algo/eccentricity G1 :top-right)
     (algo/radius G1)
     (algo/diameter G1)]))

; now we see that all of our metrics work, but :top-left has short paths,
; and the other paths need to loop through the :outside node.

(defn demo []
  (let [random-graph  (generate/make-graph 10 10)
        nodes         (vec (:nodes random-graph))
        from          (rand-nth nodes)
        to            (rand-nth nodes)
        shortest-path (shortest-path random-graph from to)
        radius        (radius random-graph)
        diameter      (diameter random-graph)
        eccentricity  (eccentricity random-graph from)]
    (println "Random Graph:" random-graph)
    (println "Radius:" radius)
    (println "Diameter:" diameter)
    (println "Shortest path from" from "to" to ":" shortest-path)
    (println "Eccentricity of node" from ":" eccentricity)))

(demo)