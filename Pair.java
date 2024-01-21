public class Pair implements Comparable<Pair> {
        String name;
        int time;

        public Pair(String name, int time) {
            this.name = name;
            this.time = time;
        }

        @Override
        public int compareTo(Pair x) {
            // Compare tasks based on time
            return Integer.compare(this.time, x.time);
        }

        public String user() {
            return name;
        }
        public int time() {
            return time;
        }

        @Override
        public String toString() {
            return name + " (" + time + " minutes)";
        }

}
