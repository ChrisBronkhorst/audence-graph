(ns ar.graph
  (:import (clojure.lang PersistentQueue)))

; This link has a simple explanation of a simple Graph definition "language"
; and implementations of depth/breadth graph search algorithms.
;
; http://hueypetersen.com/posts/2013/06/25/graph-traversal-with-clojure/
;
; The code is reproduced here for readability:

(def G {:1 [:2 :3],
        :2 [:4],
        :3 [:4],
        :4 []})

(defn traverse-graph-dfs [g s]
  (loop [vertices [] explored #{s} frontier [s]]
    (if (empty? frontier)
      vertices
      (let [v (peek frontier)
            neighbors (g v)]
        (recur
          (conj vertices v)
          (into explored neighbors)
          (into (pop frontier) (remove explored neighbors)))))))

(defn seq-graph-dfs [g s]
  (letfn [(rec-dfs [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v (peek frontier)
                      neighbors (g v)]
                  (cons v (rec-dfs
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
   (rec-dfs #{s} [s])))

(defn seq-graph-bfs [g s]
  (letfn [(rec-bfs [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v (peek frontier)
                      neighbors (g v)]
                  (cons v (rec-bfs
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
    (rec-bfs #{s} (conj PersistentQueue/EMPTY s))))

(traverse-graph-dfs G :1) ; => [:1 :3 :4 :2]
(seq-graph-dfs G :1) ; => (:1 :3 :4 :2)
(seq-graph-bfs G :1) ; => (:1 :2 :3 :4)

;==================== QUESTION 1 ====================
; The author then simplifies it by recognising that only the initial data
; structure for holding the nodes traversed is different between the depth
; and breadth first implementations.

;He then abstacts that out and the result is:

(defn seq-graph [d g s]
  (letfn [(rec-seq [explored frontier]
            (lazy-seq
              (if (empty? frontier)
                nil
                (let [v (peek frontier)
                      neighbors (g v)]
                  (cons v (rec-seq
                            (into explored neighbors)
                            (into (pop frontier) (remove explored neighbors))))))))]
   (rec-seq #{s} (conj d s))))

(def seq-graph-dfs (partial seq-graph []))
(def seq-graph-bfs (partial seq-graph PersistentQueue/EMPTY))

(seq-graph-dfs G :1) ; => (:1 :3 :4 :2)
(seq-graph-bfs G :1) ; => (:1 :2 :3 :4)