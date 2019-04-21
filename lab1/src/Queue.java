import java.util.Stack;
import java.util.NoSuchElementException;

public class Queue<E> extends Stack<E> {
	private Stack<E> stk;

	public Queue() {
		stk = new Stack<>();
	}

	public boolean add(E e) throws NullPointerException {
		if (e == null) {
			throw new NullPointerException();
		}
		stk.push(e);
		return true;
	}

	public boolean offer(E e) throws NullPointerException {
		if (e == null) {
			throw new NullPointerException();
		}
		stk.push(e);
		return true;
	}

	public E remove() throws NoSuchElementException {
		if (!this.empty()) {
			return this.pop();
		} else if (!stk.empty()) {
			while (!stk.empty()) {
				this.push(stk.pop());
			}
			return this.pop();
		} else {
			throw new NoSuchElementException();
		}
	}

	public E poll() {
		if (!this.empty()) {
			return this.pop();
		} else if (!stk.empty()) {
			while (!stk.empty()) {
				this.push(stk.pop());
			}
			return this.pop();
		} else {
			return null;
		}
	}

	public E element() throws NoSuchElementException {
		if (!this.empty()) {
			return super.peek();
		} else if (!stk.empty()) {
			while (!stk.empty()) {
				this.push(stk.pop());
			}
			return super.peek();
		} else {
			throw new NoSuchElementException();
		}
	}

	public E peek() {
		if (!this.empty()) {
			return super.peek();
		} else if (!stk.empty()) {
			while (!stk.empty()) {
				this.push(stk.pop());
			}
			return super.peek();
		} else {
			return null;
		}
	}

	public void show(String str) {
		System.out.println(str);
		System.out.printf("Input Stack:");
		System.out.println(stk.toString());
		System.out.printf("Output Stack:");
		System.out.println(this.toString());
		System.out.println();
	}

	public static void main(String[] args) {
		Queue<Integer> MyQueue = new Queue<>();

		// Start test
		System.out.println("HUST Java lab 01 -- Queue Test Start");
		MyQueue.show("Initial Queue Status");

		// Test add
		System.out.println("[TEST 1] add test");
		for (int i = 1; i <= 20; i++)
			MyQueue.add(i);
		MyQueue.show("add 1 ~ 20:");

		// Test poll
		System.out.println("[TEST 2] poll test");
		for (int i = 1; i <= 5; i++)
			MyQueue.poll();
		MyQueue.show("poll 5 elements:");

		// Test offer
		System.out.println("[TEST 3] offer test");
		for (int i = 21; i <= 40; i++)
			MyQueue.offer(i);
		MyQueue.show("offer 21 ~ 40:");

		// Test remove
		System.out.println("[TEST 4] remove test");
		for (int i = 1; i <= 20; i++) {
			try {
				MyQueue.remove();
			} catch (NoSuchElementException e) {
				System.out.println("Catch NoSuchElementException");
			}
		}
		MyQueue.show("remove 20 elements:");
		for (int i = 1; i <= 16; i++) {
			try {
				MyQueue.remove();
			} catch (NoSuchElementException e) {
				System.out.println("Catch NoSuchElementException in remove()");
			}
		}
		MyQueue.show("remove 16 elements:");

		// Test element
		System.out.println("[TEST 5] element test");
		for (int i = 1; i <= 20; i++)
			MyQueue.add(i);
		MyQueue.show("add 1 ~ 20:");
		System.out.println("remove 20 times and element 21 times");
		for (int i = 1; i <= 20; i++) {
			System.out.printf("%d ", MyQueue.element());
			MyQueue.remove();
		}
		System.out.println();
		try {
			System.out.printf("%d ", MyQueue.element());
		} catch (NoSuchElementException e) {
			System.out.println("Catch NoSuchElementException in element()");
		}
		MyQueue.show("remove 20 elements:");

		// Test peek
		System.out.println("[TEST 6] peek test");
		for (int i = 1; i <= 20; i++)
			MyQueue.add(i);
		MyQueue.show("add 1 ~ 20:");
		System.out.println("remove 20 times and peek 21 times");
		for (int i = 1; i <= 20; i++) {
			System.out.printf("%d ", MyQueue.element());
			MyQueue.remove();
		}
		System.out.println();
		if (MyQueue.peek() == null) {
			System.out.println("peek returns null");
		}
		MyQueue.show("remove 20 elements:");

		System.out.println("All Test passed!");
	}
}