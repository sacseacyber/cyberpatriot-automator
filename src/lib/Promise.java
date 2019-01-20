package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * A function that handles a value being found
 *
 * @param <T>
 */
interface PromiseResolve<T> {
	void resolve(T value);
}

/**
 * A function to handle a promise rejection
 */
interface PromiseReject {
	void reject(Exception ex);
}

/**
 * The function that a Promise uses to get its value
 *
 * Gets run in a separate thread
 *
 * @param <T>
 */
interface PromiseCallback<T> {
	void accept(PromiseResolve<T> resolve, PromiseReject reject);
}

/**
 * Implement the Promise class from JavaScript
 *
 * A Promise represents a promise of a value that does not yet exist
 */
public class Promise<T> {
	/**
     * Allows for setting and getting a non-final value in a lambda function
	 *
	 * @param <T> The type of the value beings tored
	 */
	private static class ValueHolder<T> {
		private T value;

		T getValue() {
			return value;
		}

		void setValue(T value) {
			this.value = value;
		}
	}

	/**
	 * A method that allows for waiting for a value to appear
	 *
	 * @param promise The promise to wait for
	 * @param <T> The type of the promise
	 * @return The promise value
	 * @throws InterruptedException for the thread being waited upon
	 */
	public static <T> T await(Promise<T> promise) throws InterruptedException {
		ValueHolder<T> valueHolder = new ValueHolder<>();

		Thread sub = new Thread(() -> promise.then(valueHolder::setValue));

		// Await the promise
		sub.run();
		sub.wait();

		return valueHolder.getValue();
	}

	/**
	 * Creates a promise that only resolves when all of the provided promises resolve
	 *
	 * Resolves with array T[] when all promises resolve
	 * Rejects with an exception when a single promise rejects
	 *
	 * @param promises The array of promises to wait for
	 * @param <T> The type of the promise
	 * @return the new promise
	 */
	@SuppressWarnings("unchecked")
	public static <T> Promise<T[]> all(Promise<T>[] promises) {
		return new Promise<>((res, rej) -> {
			// AtomicInteger is used due to the nature of multithreaded programs,
			// to provide thread safety
			AtomicInteger count = new AtomicInteger(0);

			// Used to create an array of T the size of the promises array
			T[] returnValue = (T[]) new Object[promises.length];

			// A simple function that will be used for each of the promises
			Function<Integer, PromiseResolve<T>> success = index ->
				val -> {
					returnValue[index] = val;
					count.incrementAndGet();

					if (count.get() == returnValue.length) {
						res.resolve(returnValue);
					}
				};

			for (int i = 0; i < promises.length; i++) {
				promises[i].then(success.apply(i), rej);
			}
		});
	}

	/**
	 * Races promises
	 *
	 * The first resolution resolves the whole promise, returning just the value of the
	 * first promise
	 *
	 * @param promises the promises to race
	 * @param <T> the type of the promises
	 * @return a promise for the race of promises
	 */
	public static <T> Promise<T> race(Promise<T>[] promises) {
		return new Promise<>((res, rej) -> {
			for (Promise<T> promise : promises) {
				promise.then(res, rej);
			}
		});
	}

	private Exception error = null;
	private T value = null;

	private List<PromiseResolve<T>> resolvedHandlers = new ArrayList<>();
	private List<PromiseReject> errHandlers = new ArrayList<>();

	public Promise(PromiseCallback<T> callback) {
		new Thread(() -> callback.accept(
				val -> {
					// Only resolve or reject once
					if (this.value != null || this.error != null) {
						return;
					}

					// Store it for later, for future .then() calls
					this.value = val;

					for (PromiseResolve<T> cBack : this.resolvedHandlers) {
						cBack.resolve(val);
					}
				},
				err -> {
					// Only resolve or reject once
					if (this.value != null || this.error != null) {
						return;
					}

					this.error = err;

					for (PromiseReject cBack : this.errHandlers) {
						cBack.reject(err);
					}
				}
		)).run();
	}

	/**
	 * Used for handling when the value exists
	 *
	 * @param resolver The callback to access the value
	 */
	public void then(PromiseResolve<T> resolver) {
		// Error already thrown, cannot be resolved
		if (this.error != null) {
			return;
		}

		// Not yet resolved
		if (this.value == null) {
			this.resolvedHandlers.add(resolver);
		} else {
			resolver.resolve(this.value);
		}
	}

	/**
	 * Used for handling when the value exists
	 *
	 * Can also handle when an error crops up
	 *
	 * @param resolver The callback to handle the value
	 * @param promiseReject The callback to handle the error
	 */
	public void then(PromiseResolve<T> resolver, PromiseReject promiseReject) {
		if (this.error != null) {
			promiseReject.reject(this.error);
			return;
		}

		if (this.value == null) {
			this.resolvedHandlers.add(resolver);
			this.errHandlers.add(promiseReject);
		} else {
			resolver.resolve(this.value);
		}
	}

	/**
	 * Implements Promise.catch from JS, but catch is reserved
	 *
	 * @param promiseReject The callback to handle the exception
	 */
	public void catchException(PromiseReject promiseReject) {
		if (this.error != null) {
			promiseReject.reject(this.error);
			return;
		}

		if (this.value == null) {
			this.errHandlers.add(promiseReject);
		}
	}
}
