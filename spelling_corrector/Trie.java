package spell;

/**
 * Created by Admin on 1/20/17.
 */

public class Trie implements ITrie {
    int wordCount;
    int nodeCount;
    TrieNode root;

    public Trie()
    {
        root = new TrieNode();
        nodeCount++;
    }

    public TrieNode getRoot() {
        return root;
    }

    /**
     * Your trie node class should implement the ITrie.INode interface
     */
    public class TrieNode implements ITrie.INode {
        int count;
        TrieNode[] children;

        TrieNode(){
            children = new TrieNode[26];
        }

        /**
         * Returns the frequency count for the word represented by the node
         *
         * @return The frequency count for the word represented by the node
         */
        public int getValue(){
            return count;
        }
    }

    /**
     * Returns the number of unique words in the trie
     *
     * @return The number of unique words in the trie
     */
    public int getWordCount(){
        return wordCount;
    }

    /**
     * Returns the number of nodes in the trie
     *
     * @return The number of nodes in the trie
     */
    public int getNodeCount(){
        return nodeCount;
    }

    /**
     * Returns corrisponding index for char uses aski lowercase
     *
     * @return The corrilating index for char
     */
    public int CharToIndex(char ch)
    {
        int index = (int)ch - 97;
        return index;
    }

    /**
     * Returns corresponding char for number uses aski lowercase
     *
     * @return The correlating char for index
     */
    public char indexToChar (int index){
        char ch = (char)(index + 97);
        return ch;
    }

    /**
     * Adds the specified word to the trie (if necessary) and increments the word's frequency count
     *
     * @param word The word being added to the trie
     */
    public void add(String word){
       word = word.toLowerCase();
        if(word.length() > 0) {

            StringBuilder sb_word = new StringBuilder();
            sb_word.append(word);

            int index = CharToIndex(sb_word.charAt(0));
            if(root.children[index] == null) {
                root.children[index] = new TrieNode();
                nodeCount++;
            }

            addHelper(root.children[index], sb_word.delete(0,1));
        }

    }

    public void addHelper(TrieNode node, StringBuilder word)
    {
        if(word.length() > 0){
            int index = CharToIndex(word.charAt(0));

            if(node.children[index] == null) {
                node.children[index] = new TrieNode();
                nodeCount++;
            }

                addHelper(node.children[index], word.delete(0,1));
        }
        else{
            node.count++;
            wordCount++;
        }
    }

    /**
     * Searches the trie for the specified word
     *
     * @param word The word being searched for
     *
     * @return A reference to the trie node that represents the word,
     * 			or null if the word is not in the trie
     */
    public TrieNode find(String word){
        word = word.toLowerCase();
        if(word.length() > 0) {

            StringBuilder sb_word = new StringBuilder();

            sb_word.append(word);
            int index = CharToIndex(sb_word.charAt(0));

            if(root.children[index] == null)
                return null;

            if(word.length() == 1 && root.count > 0)
                return root;

            if(word.length() > 1)
                if(root.children[index] != null)
                    return findHelper(root.children[index], sb_word.delete(0,1));

        }
        return null;
    }

    public TrieNode findHelper(TrieNode node, StringBuilder word)
    {
        if(word.length() == 0 && node.count > 0)
            return node;
        if(word.length() > 0)
        {
            int index = CharToIndex(word.charAt(0));
            if(node.children[index] != null)
                return findHelper(node.children[index], word.delete(0, 1));
        }

        return null;
    }



    /**
     * The toString specification is as follows:
     * For each word, in alphabetical order:
     * <word>\n
     */
    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < 26; i++) {
            word.setLength(0);
            if(root.children[i] != null)
                toStringHelper(root.children[i], word, output, i);
        }

        return output.toString();
    }

    public void toStringHelper(TrieNode node, StringBuilder word, StringBuilder output, int j){
        //check greatrer than 0
        //build stringbuilder
        word.append(indexToChar(j));

        if(node.count > 0)
        {
            output.append(word);
            output.append("\n");

        }

        for (int i = 0; i < 26; i++) {
            if(node.children[i] != null)
                toStringHelper(node.children[i], word, output, i);
        }
        word.deleteCharAt(word.length()-1);
    }


    @Override
    public int hashCode(){
        return wordCount * nodeCount * 7;

    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(this == o)
            return true;
        if(this.getClass() != o.getClass())
            return false;
        
        Trie new_trie = (Trie)o;
        
        if(wordCount == new_trie.getWordCount() || nodeCount != new_trie.getNodeCount())
            return false;

        if(new_trie.getRoot() == null)
            return false;

        return equalsHelper(new_trie);
    }

    public boolean equalsHelper(Trie trie){
        for (int i = 0; i < 26; i++) {
            if(root.children[i] != null) {
                if (trie.getRoot().children[i] != null) {
                    if (root.children[i].getValue() == trie.getRoot().children[i].getValue())
                        if(equalsRecursionHelper(root.children[i], trie.getRoot().children[i]))
                            return false;
                }
                else return false;
            }

        }
       return true;
    }

    public boolean equalsRecursionHelper(TrieNode ogNode, TrieNode newNode){
        for (int i = 0; i < 26; i++) {
            if(ogNode.children[i] != null) {
                if (newNode.children[i] != null) {
                    if (ogNode.children[i].getValue() == newNode.children[i].getValue())
                        if(equalsRecursionHelper(ogNode.children[i], newNode.children[i]) == false)
                            return false;
                }
                else return false;
            }

        }
        return true;
    }




	/*
	 * EXAMPLE:
	 *
	 * public class Words implements ITrie {
	 *
	 * 		public void add(String word) { ... }
	 *
	 * 		public ITrie.INode find(String word) { ... }
	 *
	 * 		public int getWordCount() { ... }
	 *
	 * 		public int getNodeCount() { ... }
	 *
	 *		@Override
	 *		public String toString() { ... }
	 *
	 *		@Override
	 *		public int hashCode() { ... }
	 *
	 *		@Override
	 *		public boolean equals(Object o) { ... }
	 *
	 * }
	 *
	 * public class WordNode implements ITrie.INode {
	 *
	 * 		public int getValue() { ... }
	 * }
	 *
	 */
}
