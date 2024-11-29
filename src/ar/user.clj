(ns ar.user
  [:require [ar.algo :as algo]
            [ar.generate :as generate]
            [ar.graph :as graph :refer [empty-graph add-edge count-edges]]])

; This link has a simple explanation of a simple Graph definition "language"
; and implementations of depth/breadth graph search algorithms.
;
; http://hueypetersen.com/posts/2013/06/25/graph-traversal-with-clojure/
;
; The code is reproduced here for readability:

;(def G {:1 [(edge :2 1) (edge :3 1)],
;        :2 [(edge :4 3)],
;        :3 [(edge :4 5)],
;        :4 []})

(def G1 (-> (empty-graph)
            (add-edge :1 :2 1)
            (add-edge :1 :3 1)
            (add-edge :2 :4 3)
            (add-edge :3 :4 5)))

(def G2 (generate/make-graph 10 30)) ; uses integer node ids

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
(count-edges (generate/make-graph 10 90))
