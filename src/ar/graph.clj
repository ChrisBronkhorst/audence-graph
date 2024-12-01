(ns ar.graph)

; Directed Graph. Keeps track of outgoing and incomming edges.
; Keeps track of edge strength at the leaf of the edge maps.
; there is duplication for the sake of performance
; If there is no edge strength, it is assumed to be 1.

(defn empty-graph []
  {:nodes #{}
   :in    {}
   :out   {}})

(defn add-node [g node] (update g :nodes conj node))
(defn add-nodes [g new-nodes] (update g :nodes (fn [nodes] (into nodes new-nodes))))

(defn add-edge
  ([g from to] (add-edge g from to 1))
  ([g from to weight]
   ; no self loops
   (assert (not= from to) "No self loops allowed")
   (-> g
       (add-node from) ; adding and edge adds a node
       (add-node to)   ; adding and edge adds a node
       (assoc-in [:out from to] weight)
       (assoc-in [:in to from] weight))))

(defn remove-edge [g from to]
  (-> g
      (update-in [:out from] dissoc to)
      (update-in [:in to] dissoc from)))

(defn count-edges [g]
  (->> (:out g)
       (vals)
       (map count)
       (reduce + 0)))

(defn neighbours [g node] (get-in g [:out node]))