run with:
lein repl

should output something like:
Random Graph: {:nodes #{7 1 4 6 3 2 9 5 10 8}, :in {7 {2 38}, 1 {2 20}, 6 {9 59}, 3 {4 98}, 2 {4 12}, 9 {4 84, 3 42}, 5 {8 72}, 10 {3 54}, 8 {4 99}}, :out {4 {2 12, 9 84, 8 99, 3 98}, 9 {6 59}, 3 {10 54, 9 42}, 2 {1 20, 7 38}, 8 {5 72}}}
Radius: 171
Diameter: 9223372036854775807
Shortest path from 8 to 9 : nil
Eccentricity of node 8 : 9223372036854775807
