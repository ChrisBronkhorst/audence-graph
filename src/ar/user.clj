(ns ar.user
  [:require [ar.algo :as algo]
            [ar.generate :as generate]
            [ar.graph :refer [add-edge graph]]])

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

(def G (-> (graph)
           (add-edge :1 :2 1)
           (add-edge :1 :3 1)
           (add-edge :2 :4 3)
           (add-edge :3 :4 5)))


(comment
  (algo/traverse-graph-dfs G :1) ; => [:1 :3 :4 :2]
  (algo/seq-graph-dfs G :1) ; => (:1 :3 :4 :2)
  (algo/seq-graph-bfs G :1) ; => (:1 :2 :3 :4)

  (algo/seq-graph-dfs G :1) ; => (:1 :3 :4 :2)
  (algo/seq-graph-bfs G :1) ; => (:1 :2 :3 :4)

  nil)

;==================== QUESTION 1 ====================
;1. Extend the graph definition to include a weight between graph edges