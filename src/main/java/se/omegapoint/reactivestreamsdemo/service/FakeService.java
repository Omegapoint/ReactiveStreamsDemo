package se.omegapoint.reactivestreamsdemo.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class FakeService
{
    private static final Random random = new Random();
    private long lastCall = 0L;
    private final long startTime = System.nanoTime();
    private final long processTime = 5 + random.nextInt(15 - 5 + 1);
    private boolean analyticsSent = false;
    private boolean processCompleted = false;
    private Map<String, String> userData = new HashMap<>(Map.of("user1", "bombs and terrorist plans", "user2", "password 123456"));
    private final List<String> superlatives = List.of("Super", "Fantastic", "Ultra", "Mega", "Hyper");
    private static final String[] sampleStrings = {"apple", "banana", "orange", "grape", "mango"};
    private final String currentSuperlative = superlatives.get(random.nextInt(superlatives.size()));
    private boolean valueMissingAtThisNode = true;
    private final Map<String, Object> storage;
    private final String offendingUser;

    public FakeService()
    {
        Map<String, Object> resultMap = new HashMap<>();

        int nUsers = 10;
        for (int i = 0; i < nUsers; i++) {
            String key = "user" + (i + 1);
            Object value = randomValue(1);
            resultMap.put(key, value);
        }

        int offendingUser = 1 + random.nextInt(nUsers);
        this.offendingUser = "user" + offendingUser;
        Object o = resultMap.get(this.offendingUser);
        while (true)
        {
            if (o instanceof Map<?,?>)
            {
                o = ((Map<?, ?>) o).get(((Map<?, ?>) o).keySet().stream().findAny().get());
            }
            else
            {
                ((Set) o).add("illegalDocuments");
                break;
            }
        }

        this.storage = resultMap;
    }


    private static Object randomValue(int depth) {
        int randomType = random.nextInt(2);
        if (randomType == 0) {
            return randomSet();
        } else {
            if (depth > 3) { // Limit recursion depth to avoid StackOverflow
                return randomSet();
            } else {
                Map<String, Object> nestedMap = new HashMap<>();
                String key = "nested" + (depth + 1);
                Object value = randomValue(depth + 1);
                nestedMap.put(key, value);
                return nestedMap;
            }
        }
    }
    private static Set<String> randomSet() {
        Set<String> resultSet = new HashSet<>();
        int setSize = random.nextInt(sampleStrings.length) + 1;

        for (int i = 0; i < setSize; i++) {
            String randomString = sampleStrings[random.nextInt(sampleStrings.length)];
            resultSet.add(randomString);
        }

        return resultSet;
    }

    public boolean offendingUser(String user)
    {
        return user.equals(offendingUser);
    }

    public Mono<Boolean> checkIfProcessCompleted()
    {
        return Mono.defer(() -> {
            long time = System.nanoTime();
            if (time - this.lastCall < 1_000_000_000)
            {
                return Mono.error(new RuntimeException("Rate limit: 1 request per second"));
            }

            this.lastCall = time;
            if (time - startTime > processTime * 1_000_000_000L)
            {
                processCompleted = true;
            }
            return Mono.just(processCompleted);
        });
    }

    public void sendAnalyticsToDatabase(String input)
    {
        Mono.fromRunnable(() -> this.analyticsSent = true).subscribe();
    }

    public boolean analyticsSent()
    {
        return this.analyticsSent;
    }

    public boolean processCompleted()
    {
        return this.processCompleted;
    }

    public Mono<String> enrich(String input)
    {
        return Mono.just(currentSuperlative + " " + input);
    }

    public boolean isEnriched(String input)
    {
        return input.contains(currentSuperlative);
    }

    public Mono<Integer> divide(Integer numerator, Integer denominator)
    {
        return Mono.just(numerator / denominator);
    }

    public Mono<Long> currentNanoTime()
    {
        return Mono.defer(() -> Mono.just(System.nanoTime()));
    }

    public Flux<String> getData(String path)
    {
        if (path.isEmpty())
        {
            return Flux.fromIterable(storage.keySet());
        }

        String[] split = path.split("/");
        Map<?, ?> current = storage;

        for (String s : split)
        {
            Object content = current.get(s);

            if (content instanceof Set)
            {
                if (((Set<String>) content).stream().anyMatch(path::endsWith))
                {
                    return Flux.empty();
                }
                return Flux.fromIterable((Set) content).map(t -> path + "/" + t);
            }

            if (content instanceof String)
            {
                if (path.endsWith((String) content))
                {
                    return Flux.empty();
                }

                return Flux.just(path + "/" + content);
            }

            current = (Map<?, ?>) content;
        }

        return Flux.fromIterable(current.keySet())
            .map(o -> path + "/" + o);
    }


    public Flux<Page> firstPage()
    {
        return Flux.just(new Page("Data of page " + 0, 0));
    }

    public Mono<Page> nextPage(Page currentPage)
    {
        return Mono.just(new Page("Data of page " + currentPage.page + 1, currentPage.page + 1));
    }

    public Mono<Void> archiveDataIfNeeded(String userId)
    {
        String data = this.userData.get(userId);

        boolean dataNeedsTobePreserved = data.contains("terrorist");
        /* Imagine we send the data off to some database */

        return Mono.empty();
    }

    public Mono<Void> deleteAllUserData()
    {
        this.userData = new HashMap<>();

        return Mono.empty();
    }

    public boolean userDataDeleted()
    {
        return this.userData.size() == 0;
    }


    public class Page {
        public String data;
        public int page;

        protected Page(String data, int page)
        {
            this.data = "data";
            this.page = page;
        }
    }

    public Mono<String> whatToEatToday(int i)
    {
        String s = this.superlatives.get(i);
        if (s.equals(this.currentSuperlative) && this.valueMissingAtThisNode)
        {
            this.valueMissingAtThisNode = false;
            return Mono.error(new RuntimeException("500 Internal Error"));
        }
        return Mono.just(s + " " + sampleStrings[i]);
    }

    public Mono<Integer> slowThing(Integer number) {
        return Mono.fromCallable(() -> {
            Thread.sleep(100);
            return number;
        });
    }
}
