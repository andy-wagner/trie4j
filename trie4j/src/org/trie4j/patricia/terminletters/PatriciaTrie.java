package org.trie4j.patricia.terminletters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.trie4j.Trie;
import org.trie4j.TrieVisitor;
import org.trie4j.util.Pair;

@Deprecated
public class PatriciaTrie implements Trie{
	@Override
	public boolean contains(String word) {
		return root.contains(word.toCharArray(), 0);
	}

	@Override
	public Iterable<String> commonPrefixSearch(final String query) {
		if(query.length() == 0) return new ArrayList<String>(0);
		return new Iterable<String>(){
			{
				this.queryChars = query.toCharArray();
			}
			private char[] queryChars;
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					private int cur;
					private StringBuilder currentChars = new StringBuilder();
					private Node current = root;
					private String next;
					{
						cur = 0;
						findNext();
					}
					private void findNext(){
						next = null;
						while(next == null){
//*
							Node n = current.getChild(queryChars[cur]);
							if(n == null) return;
							Pair<Boolean, Integer> r = n.compareLetters(queryChars, cur);
							if(!r.getFirst()) return;
							if(r.getSecond() > 0) return;
							int d = r.getSecond();
							if(d > 0) return;
							d *= -1;

							String b = new String(queryChars, cur, d);
							if(n.isTerminated()){
								next = currentChars + b;
							}
							cur += d;
							currentChars.append(b);
							current = n;
							
/*/
							int rest = queryChars.length - cur;
							Node n = current.getChild(queryChars[cur]);
							if(n == null) return;
							char[] l = n.getLetters();
							int len = l.length;
							boolean t = false;
							if(l[len - 1] == 0xffff){
								t = true;
								len--;
							}
							if(n.getChildren() == null){
								t = true;
							}
							if(rest < len) return;
							for(int i = 0; i < len; i++){
								if(l[i] != queryChars[cur++])  return;
							}
							String b = new String(l, 0, len);
							if(t){
								next = currentChars + b;
							}
							currentChars.append(b);
							current = n;
//*/
						}
					}
					@Override
					public boolean hasNext() {
						return next != null;
					}
					@Override
					public String next() {
						String ret = next;
						if(ret == null){
							throw new NoSuchElementException();
						}
						findNext();
						return ret;
					}
					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		return null;
	}

	public void insert(String text){
		char[] letters = text.toCharArray();
		if(root == null){
			root = new Node(letters);
			return;
		}
		root.insertChild(letters, 0);
	}
	public void visit(TrieVisitor visitor){
		root.visit(visitor, 0);
	}

	public Node getRoot(){
		return root;
	}

	private Node root;
}
