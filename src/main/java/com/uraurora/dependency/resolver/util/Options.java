package com.uraurora.dependency.resolver.util;

import com.google.common.collect.*;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 20:02
 * @description :
 */
public abstract class Options {
    public static <T> List<T> emptyList(){
        return new ArrayList<>();
    }

    public static <T> Set<T> emptySet(){
        return new HashSet<>();
    }

    public static <K, V> Map<K, V> emptyMap(){
        return new HashMap<>();
    }

    public static <T> Deque<T> emptyDeque(){
        return new ArrayDeque<>();
    }

    public static final String emptyString = "";

    public static  <T> boolean isEmpty(Collection<? extends T> collection){
        return collection == null || collection.isEmpty();
    }

    public static  <T> boolean isNotEmpty(Collection<? extends T> collection){
        return !isEmpty(collection);
    }

    //<editor-fold desc="高阶函数">
    /**
     * 功能类似kotlin中的with，接受一个T类型的接受者，如果其为null则返回null
     * 不为空则返回func函数执行后的结果
     * @param object 接受者类型
     * @param func 函数
     * @param <T> 接受者类型
     * @param <R> 返回类型
     * @return 处理后的结果
     */
    public static  <T, R> R map(T object, Function<? super T, ? extends R> func){
        return object != null ? func.apply(object) : null;
    }

    /**
     * 功能类似kotlin中的let，接受一个T类型的接受者，如果其为null则不作任何操作
     * 不为空则执行func函数
     * @param object 接受者对象
     * @param func 函数
     * @param <T> 接受者类型
     */
    public static  <T> void let(T object, Consumer<? super T> func){
        if (object != null) {
            func.accept(object);
        }
    }

    public static  <T> void letCollection(Collection<T> collection, Consumer<? super Collection<T>> func){
        if (collection != null && collection.size()>0) {
            func.accept(collection);
        }
    }

    /**
     * 功能类似kotlin中的with，接受一个T类型的接受者，如果其为null则返回null
     * 不为空则对其引用进行操作，之后返回其引用
     * @param object 接收者对象
     * @param func 处理函数
     * @param <T> 接收者类型
     * @return 处理过的接收者对象引用
     */
    public static  <T> T with(T object, Consumer<? super T> func){
        if (object != null) {
            func.accept(object);
            return object;
        } else {
            return null;
        }
    }

    /**
     * 功能类似kotlin中的with，包含了一个 {@link Predicate} 的条件，接受一个T类型的接受者，
     * 如果其不为null且满足条件则对其引用进行操作，之后返回其引用，
     * 为空则返回空
     * @param object
     * @param condition
     * @param func
     * @param <T>
     * @return
     */
    public static  <T> T with(T object, Predicate<? super T> condition, Consumer<? super T> func){
        if (object != null && condition.test(object)) {
            func.accept(object);
            return object;
        } else {
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="非空对象默认值">
    /**
     * 功能类似kotlin中的?:操作符，用在最后包装对象
     * 如果对象为null则返回指定的默认值other，如果不为空则返回自身
     * @param object 接收者对象
     * @param other 默认值
     * @param <T> 接收者类型
     * @return 对象自身或默认值
     */
    public static  <T> T orDefault(T object, T other){
        return object != null ? object : other;
    }

    /**
     * 功能类似kotlin中的简化版的if else表达式，用在最后包装对象
     * 如果对象不为空且满足条件则返回自身，否则返回指定的默认值other
     * @param object 接收者对象
     * @param condition 条件
     * @param other 默认值
     * @param <T> 接收者类型
     * @return 对象自身或默认值
     */
    public static  <T> T orDefault(T object, Predicate<? super T> condition, T other){
        return object != null && condition.test(object) ? object : other;
    }

    public static  <T> T orElse(T object, Callable<T> callable) throws Exception {
        return orDefault(object, callable.call());
    }

    public static  <T> T orElse(T object, Predicate<? super T> condition, Callable<T> callable) throws Exception {
        return orDefault(object, condition, callable.call());
    }
    //</editor-fold>

    /**
     * 相当于kotlin中的!!，不予许为null，为null会抛出异常
     * @param object 接收者对象
     * @param <T> 接收者类型
     * @return 接收者对象引用
     * @throws NullPointerException 空指针异常
     */
    public static <T> T notNull(T object) throws NullPointerException{
        return Objects.requireNonNull(object);
    }

    //<editor-fold desc="非空类型转换">

    /**
     * 类型转换逻辑，如果无法转换则返回null
     * @param obj 对象
     * @param clazz 类型
     * @param <T> 类型参数
     * @return 转化后的对象或者null
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, Class<T> clazz){
        if (obj != null && !clazz.isInstance(obj)) {
            return null;
        } else{
            return (T) obj;
        }
    }

    /**
     *
     * @param obj 对象
     * @param clazz 类型
     * @param defaultValue 默认值
     * @param <T> 类型参数
     * @return 转化后的对象或者默认值
     */
    public static <T> T cast(Object obj, Class<T> clazz, T defaultValue){
        return orDefault(cast(obj, clazz), defaultValue);
    }
    //</editor-fold>








    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> List<T> listOf(T... objs){
        return orDefault(map(objs, Lists::newArrayList), emptyList());
    }

    public static <T> List<T> listOf(Iterable<? extends T> elements){
        return orDefault(map(elements, Lists::newArrayList), emptyList());
    }

    @SafeVarargs
    public static <T> List<T> linkedListOf(T... objs){
        return orDefault(map(objs, o->{
            List<T> res = Lists.newLinkedList();
            Collections.addAll(res, o);
            return res;
        }), emptyList());
    }
    /**
     * 限定容量大小的链表实现的列表，策略为达到容量上限后就不会再添加元素
     * 当有新元素添加时，会判断是否达到设置的容量上限，未达到则可以继续添加，达到的话就不会再添加元素
     * @param limit 容量上限
     * @param <T> 元素类型
     * @return 具有容量上限的列表，推荐用来遍历元素而不作查询
     */
    public static <T> List<T> limitedList(int limit){
        return new LimitedList<>(limit, false);
    }

    public static <T> List<T> limitedListOf(Collection<? extends T> col, int limit){
        LimitedList<T> res = new LimitedList<>(limit, false);
        if(isNotEmpty(col)){
            res.addAll(col);
        }
        return res;
    }

    /**
     * 限定容量大小的链表实现的列表，策略为删除最前面的元素，效果类似LRU
     * 当有新元素添加时，会判断是否达到设置的容量上限，未达到则可以继续添加，达到的话按照移除最前端的元素策略删除元素
     * @param limit 容量上限
     * @param <T> 元素类型
     * @return 具有容量上限的列表，推荐用来遍历元素而不作查询
     */
    public static <T> List<T> limitedListWithRemoveFirst(int limit){
        return new LimitedList<>(limit, true);
    }


    public static <T> List<T> limitedListWithRemoveFirstOf(Collection<? extends T> col, int limit){
        LimitedList<T> res = new LimitedList<>(limit, true);
        if(isNotEmpty(col)){
            res.addAll(col);
        }
        return res;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> Set<T> setOf(T... objs){
        return orDefault(map(objs, Sets::newHashSet), emptySet());
    }

    public static <T> Set<T> setOf(Iterable<? extends T> elements){
        return orDefault(map(elements, Sets::newHashSet), emptySet());
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs){
        return orDefault(map(pairs, ps->{
            Map<K, V> res = emptyMap();
            for (Pair<K, V> pair : pairs) {
                res.put(pair.getVal1(), pair.getVal2());
            }
            return res;
        }), emptyMap());
    }

    public static <K, V> Map<K, V> limitedMap(int limit){
        return new LimitedMap<>(limit);
    }

    public static <A, B> Pair<A, B> pair(A a, B b){
        return Pair.of(a, b);
    }

    public static <K, V> Map<K, V> associateWith(Iterable<K> keys, Function<? super K, V> func){
        return buildMap(map-> keys.forEach(key->
                let(key, k->map.put(k, func.apply(k)))
        ));
    }

    /**
     * 功能同kotlin中的buildString，接收一个{@link Consumer} 类型参数作为处理函数
     * 处理预先生成的 {@link StringBuilder} 对象，该函数最后返回处理后的字符串
     * @param action 处理动作
     * @return 构建的字符串
     */
    public static String buildString(Consumer<? super StringBuilder> action){
        return with(new StringBuilder(), action).toString();
    }

    /**
     * 构造join的字符串，设置中间分隔符和前缀和后缀
     * @param elements 可迭代元素
     * @param prefix 前缀
     * @param suffix 后缀
     * @param step 分隔符
     * @param <T> 元素类型
     * @return 结果字符串
     */
    public static <T> String joinString(Iterable<T> elements, String prefix, String suffix, String step) {
        return buildString(builder -> {
            builder.append(prefix);
            Iterator<T> it = elements.iterator();
            while (it.hasNext()) {
                final T next = it.next();
                if(next!=null) { builder.append(next); }
                if (it.hasNext()) {
                    builder.append(step);
                }
            }
            builder.append(suffix);
        });
    }

    public static <T> String joinString(Iterable<T> elements) {
        return buildString(builder -> {
            Iterator<T> it = elements.iterator();
            while (it.hasNext()) {
                final T next = it.next();
                if(next!=null) { builder.append(next); }
                if (it.hasNext()) {
                    builder.append(",");
                }
            }
        });
    }

    /**
     * 构建一个{@link Map}对象
     * @param func 构建过程
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 构建好的Map对象，类型为{@link HashMap}
     */
    public static <K, V> Map<K, V> buildMap(Consumer<? super Map<K, V>> func){
        return with(emptyMap(), func);
    }
    /**
     * 构建一个{@link Map}对象
     * @param map 传进来的Map对象
     * @param func 构建过程
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 构建好的Map对象
     */
    public static <K, V> Map<K, V> buildMap(Map<K, V> map, Consumer<? super Map<K, V>> func){
        return with(map, func);
    }

    public static <T> List<T> buildList(Consumer<? super List<T>> func){
        return with(emptyList(), func);
    }

    public static <T> List<T> buildList(List<T> list, Consumer<? super List<T>> func){
        return with(list, func);
    }

    public static <T> Set<T> buildSet(Consumer<? super Set<T>> func){
        return with(emptySet(), func);
    }

    public static <T> Set<T> buildSet(Set<T> set, Consumer<? super Set<T>> func){
        return with(set, func);
    }

    public static <T> Deque<T> buildDeque(Deque<T> queue, Consumer<? super Deque<T>> func){
        return with(queue, func);
    }

    public static <T> Deque<T> buildDeque(Consumer<? super Deque<T>> func){
        return with(emptyDeque(), func);
    }


    public static <T, R> R fold(Collection<T> elements, R initial, BiFunction<R, ? super T, R> combine){
        R res = initial;
        for (T e : elements) {
            res = combine.apply(res, e);
        }
        return res;
    }





    public static <T extends Comparable<? super T>> Range<T> range(T c1, T c2){
        return Range.closed(c1, c2);
    }

    public static List<Integer> rangeTo(int lower, int upper, int step){
        return buildList(list->{
            for (int i = lower; i <= upper; i+=step) {
                list.add(i);
            }
        });
    }

    public static List<Integer> rangeTo(int lower, int upper){
        return rangeTo(lower, upper, 1);
    }

    /**
     * 仿照kotlin的高阶函数实现的包装类，由于java无法添加扩展函数，采用这种替代方案
     * 作为任意对象的包装类，使其具有使用高阶函数的能力，同时对其可空性进行判断，
     * 在除了 {@link Option#get()} {@link Option#apply} 以外的结束方法中，返回的都可以确保是非空的对象，
     * 里面的高阶函数方法都会对对象作非空检查，同时可以链式调用，如果对象为空则会传递到最后一个方法
     * 如{@link Option#getOrDefault(Object)} 和 {@link Option#getOrElse(Callable)}
     * @param <T> 接收者类型
     *
     */
    public static class Option<T>{

        protected T obj;

        protected Option(T obj) {
            this.obj = obj;
        }

        public static <T> Option<T> of(T obj){
            return new Option<>(obj);
        }

        /**
         * 如果包装的对象不为空则对其做某些处理
         * @param action action
         * @return
         */
        public Option<T> let(Consumer<? super T> action){
            Options.let(obj, action);
            return this;
        }

        /**
         * 功能类似于map高阶函数
         * @param func
         * @param <R>
         * @return
         */
        public <R> Option<R> map(Function<? super T, ? extends R> func){
            return Option.of(Options.map(obj, func));
        }

        /**
         * cast操作，当且仅当对象不为空且是R类型的时候才做转换，不满足此条件则传入null
         * @param clazz class类型
         * @param <R> 目标类型
         * @return 新的Option对象
         */
        public <R> Option<R> cast(Class<R> clazz){
            return Option.of(Options.cast(obj, clazz));
        }

        public Option<T> repeat(int times, Consumer<? super T> action){
            Options.with(obj, o->{
                for (int i = 0; i < times; i++) {
                    action.accept(o);
                }
            });
            return this;
        }

        public Option<T> takeIf(Predicate<? super T> condition){
            obj = Options.with(obj, condition, o->{});
            return this;
        }

        public Option<T> takeUnless(Predicate<? super T> condition){
            obj = Options.with(obj, t->!condition.test(t), t->{});
            return this;
        }

        public T getOrDefault(T other){
            return Options.orDefault(obj, other);
        }

        public T getOrDefault(Predicate<? super T> condition, T other){
            return Options.orDefault(obj, condition, other);
        }

        public T getOrElse(Callable<T> callable) throws Exception {
            return Options.orElse(obj, callable);
        }

        public T getOrElse(Predicate<? super T> condition, Callable<T> callable) throws Exception {
            return Options.orElse(obj, condition, callable);
        }

        public T get(){
            return obj;
        }

        public T apply(Consumer<? super T> consumer){
            return Options.with(obj, consumer);
        }

        public T apply(Predicate<? super T> condition, Consumer<? super T> func){
            return Options.with(obj, condition, func);
        }
    }

    public static class CollectionOption<R, T extends Collection<R>> extends Option<T>{

        private static final Predicate<Collection<?>> isNotEmpty = collection -> collection!=null && collection.size()>0;

        protected CollectionOption(T obj) {
            super(obj);
        }

        public static <R, T extends Collection<R>> CollectionOption<R, T> of(T collection){
            return new CollectionOption<>(collection);
        }

        public R fold(R initial, BinaryOperator<R> combine){
            return Options.fold(obj, initial, combine);
        }

        @Override
        public T apply(Consumer<? super T> consumer) {
            return with(obj, isNotEmpty, consumer);
        }

        @Override
        public T apply(Predicate<? super T> condition, Consumer<? super T> func) {
            return with(obj, collection-> isNotEmpty.test(collection) && condition.test(collection), func);
        }

        @Override
        public CollectionOption<R, T> let(Consumer<? super T> action) {
            with(obj, isNotEmpty, action);
            return this;
        }

        @Override
        public CollectionOption<R, T> repeat(int times, Consumer<? super T> action) {
            with(obj, isNotEmpty, collection->{
                for (int i = 0; i < times; i++) {
                    action.accept(collection);
                }
            });
            return this;
        }

        public<U> CollectionOption<U, Collection<U>> mapEach(Function<? super R, ? extends U> func){
            Collection<U> res = Options.map(obj, collection-> collection.stream().map(func).collect(Collectors.toList()));
            return CollectionOption.of(res);
        }
    }

    /**
     * 只有两个元素的不可变元组类型（final字段）
     * @param <T1>
     * @param <T2>
     */
    public static class Pair<T1, T2>{
        private final T1 val1;

        private final T2 val2;


        private Pair(T1 val1, T2 val2) {
            this.val1 = val1;
            this.val2 = val2;
        }

        public static <A, B> Pair<A, B> of(A a, B b){
            return new Pair<A, B>(a, b);
        }

        public T1 getVal1() {
            return val1;
        }

        public T2 getVal2() {
            return val2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(val1, pair.val1) &&
                    Objects.equals(val2, pair.val2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val1, val2);
        }

        @Override
        public String toString() {
            return "{" + val1 +
                    ": " + val2 +
                    "}";
        }
    }

    public static class LimitedList<E> extends ForwardingList<E> {

        private final LinkedList<E> delegate = new LinkedList<>();

        private final int limit;

        private final boolean removeFirst;

        private LimitedList(int limit, boolean removeFirst) {
            this.limit = limit;
            this.removeFirst = removeFirst;
        }

        @Override
        protected List<E> delegate() {
            return delegate;
        }

        @Override
        public boolean add(@NonNull E element) {
            if(removeFirst){
                if (delegate.size() + 1 > limit){
                    delegate.removeFirst();
                }
                return standardAdd(element);
            }else {
                if(delegate.size() + 1 <= limit){
                    return standardAdd(element);
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> collection) {
            return standardAddAll(collection);
        }
    }

    public static class LimitedMap<K, V> extends ForwardingMap<K, V> {

        private final HashMap<K, V> delegate = new HashMap<>();

        private final int limit;

        public LimitedMap(int limit) {
            this.limit = limit;
        }

        @Override
        protected Map<K, V> delegate() {
            return delegate;
        }

        @Override
        public V put(@NonNull K key, @NonNull V value) {
            if (delegate.containsKey(key) || delegate.size() + 1 <= limit) {
                return delegate.put(key, value);
            }
            return value;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            standardPutAll(map);
        }
    }
}
