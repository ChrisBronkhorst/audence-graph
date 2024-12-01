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
             (add-edge :bottom-left :bottom-right 1))]
  (time (algo/dijkstra G1 :top-left :bottom-right)))