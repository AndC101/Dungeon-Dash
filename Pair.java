/*
 * Ethan Lin & Andrew Chen
 * January 20, 2023
 * Pair is a generic class that creates a data structure with two values.
 */

public class Pair implements Comparable<Pair> {
        public String name; 
        public int time;

        //constructor
        public Pair(String name, int time) {
            this.name = name;
            this.time = time;
        }

        @Override
        //Comparable method, used to automatically sort when added to a PQ
        public int compareTo(Pair x) {
            // Compare tasks based on time
            return Integer.compare(this.time, x.time);
        }

        //returns username and time vals
        public String user() {
            return name;
        }
        public int time() {
            return time;
        }


}
