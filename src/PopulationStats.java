import java.util.*;
import java.util.stream.Collector;

public class PopulationStats {
    private final Map<Class<?>, Integer> counts;
    private final int total;
    private final double balance; // 0 to 100

    private PopulationStats(Map<Class<?>, Integer> counts) {
        this.counts = counts;
        this.total = counts.values().stream().mapToInt(Integer::intValue).sum();
        this.balance = computeBalance(counts, total);
    }

    // Shannon evenness: 100 = every species equal, 0 = one species (or none) left
    private static double computeBalance(Map<Class<?>, Integer> counts, int total) {
        int speciesCount = counts.size();
        if (total == 0 || speciesCount < 2) return 0;

        double entropy = 0;
        for (int c : counts.values()) {
            if (c == 0) continue;
            double p = (double) c / total;
            entropy -= p * Math.log(p);
        }
        return 100.0 * entropy / Math.log(speciesCount);
    }

    public int getCount(Class<?> species) { return counts.getOrDefault(species, 0); }
    public int getTotal()                 { return total; }
    public double getBalance()            { return balance; }
    public Map<Class<?>, Integer> getCounts() { return counts; }

    // The custom collector
    public static Collector<Animal, Map<Class<?>, Integer>, PopulationStats> collector(
            List<Class<? extends Animal>> trackedSpecies) {

        return Collector.of(
            // supplier: pre-seed every species at 0 so extinct ones still show up
            () -> {
                Map<Class<?>, Integer> m = new LinkedHashMap<>();
                for (Class<?> s : trackedSpecies) m.put(s, 0);
                return m;
            },
            // accumulator: only count living animals of tracked species
            (map, animal) -> {
                if (!animal.isDead() && map.containsKey(animal.getClass())) {
                    map.merge(animal.getClass(), 1, Integer::sum);
                }
            },
            // combiner (only used by parallel streams)
            (a, b) -> {
                b.forEach((k, v) -> a.merge(k, v, Integer::sum));
                return a;
            },
            // finisher
            PopulationStats::new
        );
    }
}