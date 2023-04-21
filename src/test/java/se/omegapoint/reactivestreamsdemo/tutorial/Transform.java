package se.omegapoint.reactivestreamsdemo.tutorial;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

public class Transform
{
    @Test
    public void map()
    {
        Mono<String> helloMono = Mono.just("hello");

        helloMono
                .map(s -> s.concat(" world"))
                .subscribe(System.out::println);
    }

    @Test
    public void flatMap()
    {
        Mono<String> animal = Mono.just("Cat");

        Mono<Mono<String>> animalBinomialName = animal
                .map(s -> binomialNameClient(s));

        Mono<String> animalBinomialNameFlatMapped = animal
                .flatMap(s -> binomialNameClient(s));


        animalBinomialName
                .subscribe(System.out::println); // Not very useful

        animalBinomialNameFlatMapped
                .subscribe(System.out::println);
    }

    @Test
    public void flatMapVariants()
    {
        Mono<List<String>> animals = Mono.just(List.of("Cat", "Dog", "Horse"));

        Flux<String> animalsFlux = animals.flatMapMany(list -> Flux.fromIterable(list));

        Flux<String> animalsInOrder = animalsFlux.flatMapSequential(data -> Mono.just(data));

        animals
                .subscribe(System.out::println);

        animalsFlux
                .subscribe(System.out::println);

        animalsInOrder
                .subscribe(System.out::println);
    }

    @Test
    public void aggregateFlux()
    {
        Flux<String> animals = Flux.just("Cat", "Dog", "Horse");

        Mono<List<String>> animalsList = animals.collectList(); // Nifty

        animalsList
                .subscribe(System.out::println);

        // Otherwise, same as java stream: reduce, collect, count...
    }

    @Test
    public void combinePublishers()
    {
        Flux<String> cats = Flux.just("Siamese", "Ragdoll", "Tabby");
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Rottweiler");

        Flux<String> catsAndDogsConcat = Flux.concat(cats, dogs); // Subscribe sequentially to each publisher
        Flux<String> catsAndDogsMerge = Flux.merge(cats, dogs); // Subscribe eagerly

        Flux<Tuple2<String, String>> catsAndDogsZip = Flux.zip(cats, dogs);

        catsAndDogsConcat
                .subscribe(System.out::println);

        catsAndDogsMerge
                .subscribe(System.out::println);

        catsAndDogsZip
                .subscribe(System.out::println);
    }

    @Test
    public void zipPublishers()
    {
        Flux<String> cats = Flux.just("Siamese", "Ragdoll", "Tabby");
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Rottweiler", "Poodle");

        Flux<Tuple2<String, String>> catsAndDogsZip = Flux.zip(cats, dogs);

        catsAndDogsZip
                .subscribe(System.out::println);
    }

    @Test
    public void fallbackFromEmpty()
    {
        Mono<String> emptyMono = Mono.empty();

        emptyMono
                .flatMap(s -> Mono.just("Mapped value"))
                .defaultIfEmpty("No response")
                .subscribe(System.out::println);

        emptyMono
                .flatMap(s -> Mono.just("Mapped value"))
                .switchIfEmpty(Mono.just("No response"))
                .subscribe(System.out::println);
    }

    @Test
    public void continueButIgnoreValues()
    {
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Rottweiler", "Poodle");

        dogs
                .ignoreElements()
                .subscribe(System.out::println);

        dogs
                .then(Mono.just("Do something else"))
                .subscribe(System.out::println);
    }

    @Test
    public void filter()
    {
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Bulldog", "Rottweiler", "Poodle");

        dogs
                .filter(s -> s.equals("Bulldog"))
                .subscribe(System.out::println);

        System.out.println();

        dogs
                .distinct()
                .subscribe(System.out::println);
    }

    @Test
    public void keepSubset()
    {
        Flux<String> dogs = Flux.just("Bulldog", "Golden Retriever", "Bulldog", "Rottweiler", "Poodle");

        dogs
                .take(3)
                .subscribe(System.out::println);

        System.out.println();

        dogs
                .next()
                .subscribe(System.out::println);

        System.out.println();

        dogs
                .last()
                .subscribe(System.out::println);
    }

    @Test
    public void recursion()
    {
        Flux<String> expandedHello = Mono.just("Hello")
            .expand(s -> s.length() < 10 ? Mono.just(s + "o") : Mono.empty());

        expandedHello.subscribe(System.out::println);
    }

    @Test
    public void handle()
    {
        Flux<String> animals = Flux.just("Cat", "Dog", "Horse", "House", "lastElement");

        animals
                .handle((s, synchronousSink) ->
                {
                    if (s.equals("House"))
                        synchronousSink.error(new Exception("Not an animal"));

                    else if (s.equals("lastElement"))
                        synchronousSink.complete();

                    else if (!s.equals("Horse"))
                        synchronousSink.next(s);
                })
                .subscribe(System.out::println, System.out::println);
    }

    private Mono<String> binomialNameClient(String animal)
    {
        if (animal.equals("Cat"))
            return Mono.just("Felis catus");

        return Mono.empty();
    }
}
