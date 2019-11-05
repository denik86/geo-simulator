package routing;


class TabuList
{
	int rear, front;
	int size;
	int [] tabu;

	
	TabuList(int s)
	{
		front = rear = -1;
		size = s;
		tabu = new int[s];
		for(int i = 0; i < tabu.length; i++)
			tabu[i] = -1;
	}
	
	void clear()
	{
		front = rear = -1;
		for(int i = 0; i < tabu.length; i++)
			tabu[i] = -1;
	}
	
	void add(int v)
	{
		if(check(v))
			return;
		if(front == 0 && rear == size-1){
			front = 1;
			rear = 0;
		} else if (rear == (front-1)%(size-1)) {
			rear = front;
			if(front == size-1)
				front = 0;
			else
				front++;
		} else if(front == -1) {
			front = rear = 0;
		} else if(rear == size-1 && front != 0) {
			rear = 0;
		} else {
			rear++;
		}
		tabu[rear] = v;
	}
	
	String print()
	{
		String s = "";
		if(front == -1 && rear == -1)
			return "empty";
		else {
			if(rear >= front)
			{
				for(int i = front; i <= rear; i++)
					s = s + tabu[i] + " ";
			}
			else
			{
				for(int i = front; i < size; i++)
					s = s + tabu[i] + " ";
				for(int i = 0; i <= rear; i++)
					s = s + tabu[i] + " ";
			}
		}
		return s;
	}

	int nElements()
	{	
		if(front == -1 && rear == -1)
			return 0;
		else if(rear >= front)
			return rear-front;
		else
			return size - front + rear;
	}

	
	// se la dimensione e' minore, il metodo add fa in modo che i primi elementi
	// vengano eliminati.
	TabuList resize(int newSize)
	{		
		// creo una nuova tabu list
		TabuList newTabu = new TabuList(newSize);
		
		if(rear >= front)
		{
			for(int i = front; i <= rear; i++)
				newTabu.add(tabu[i]);
		}
		else
		{
			for(int i = front; i < size; i++)
				newTabu.add(tabu[i]);
			for(int i = 0; i <= rear; i++)
				newTabu.add(tabu[i]);
		}
		return newTabu;
	}
	
	boolean check(int v)
	{
		for(int i = 0; i < tabu.length; i++)
			if(tabu[i] == v)
				return true;
		return false;
	}
}

