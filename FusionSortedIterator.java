 // Siham ait bennour 12308833
//je declare quil sagit de mon propre travail


package minebay;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Un ListIterator fusionnant plusieurs ListIterator ordonnés en interdisant les
 * opérations de modification add et set.
 * <p>
 * Un FusionSortedIterator garantie que, si les ListIterator fusionnés sont
 * ordonnés, alors ce FusionSortedIterator sera également ordonné.
 * <p>
 * Par défaut, l'ordre considéré est l'ordre naturel entre les éléments,
 * cependant un ordre alternatif peut-être spécifié à la création de l'instance.
 *
 * @param <E> le type des éléments énumérés par cet itérateur
 * @param <I> le type des itérateurs fusionnés
 * @author Marc Champesme
 * @version 8/12/2024
 * @model ListIterObserver<E> iterModel = new ListIterObserverAdapter<E>(this);
 * @invariant nextIndex() == previousIndex() + 1;
 * @invariant previousIndex() >= -1 && previousIndex() < iterModel.size());
 * @invariant nextIndex() >= 0 && nextIndex() <= iterModel.size());
 * @invariant !hasPrevious() <==> previousIndex() == -1;
 * @invariant !hasNext()() <==> nextIndex() == iterModel.size();
 * @invariant lastCalled() == nextIndex() || lastCalled() == previousIndex() ||
 * lastCalled() == -1;
 * @invariant lastCalled() >= -1 && lastCalled() < iterModel.size());
 * @invariant !iterModel.contains(null);
 * @invariant comparator() != null;
 * @invariant iterModel.isSorted(comparator ());
 * @since 2/08/2023
 */

public class FusionSortedIterator<E extends Comparable<? super E>> implements ListIterator<E> {

        private final LinkedList<ListIterator<E>> fusion;
        private int lastCalled;
        private int lastCalledIter;
        private final Comparator<? super E> cmp;

        /**
         * Initialise une instance permettant d'itérer selon l'ordre "naturel" sur tous
         * les éléments des ListIterator de la collection spécifiée. Il s'agit donc
         * d'une fusion de tous les ListIterator contenus dans la collection spécifiée.
         * Les ListIterator spécifiés sont supposés ordonnés selon l'ordre "naturel" de
         * leurs éléments.
         *
         * @param iters ensemble des ListIterator à fusionner
         * @throws NullPointerException si l'ensemble spécifié est null ou contient null
         * @requires iters != null && !iters.contains(null);
         * @ensures (\ forall ListIterator < E > iter ; iters.contains ( iter);
         * iterModel.containsAll(toList(iter)));
         * @ensures iterModel.size() == (\sum ListIterator<E> iter;
         * iters.contains(iter); size(iter));
         * @ensures (\ forall E p ; iterModel.contains ( p); (\exists ListIterator<E> iter;
         * iters.contains(iter); contains(iter, p)));
         * @ensures !hasPrevious();
         * @ensures lastCalled() == -1;
         * @ensures comparator() != null;
         */
        public FusionSortedIterator(Collection<? extends ListIterator<E>> iters) {
                this(iters, Comparator.naturalOrder());
        }

        /**
         * Initialise une instance permettant d'itérer sur tous les éléments des
         * ListIterator de la collection spécifiée selon l'ordre spécifié. Il s'agit
         * donc d'une fusion de tous les ListIterator contenus dans la collection
         * spécifiée. les ListIterator contenus dans la collection spécifiée sont
         * supposés ordonnés selon l'ordre induit par le Comparator spécifié.
         *
         * @param iters      collection des ListIterator à fusionner
         * @param comparator le comparateur à utiliser
         * @throws NullPointerException si l'ensemble spécifié est null ou contient
         *                              null, ou si le Comparator spécifié est null
         * @requires iters != null && !iters.contains(null);
         * @requires comparator != null;
         * @ensures comparator() != null;
         * @ensures comparator().equals(comparator);
         * @ensures !hasPrevious();
         * @ensures lastCalled() == -1;
         */
        public FusionSortedIterator(Collection<? extends ListIterator<E>> iters, Comparator<? super E> comparator) {
                //conditions
                if (  (comparator == null) || (iters == null)) {
                        throw new NullPointerException();
                }
                cmp = comparator; //initialisation
                fusion = new LinkedList<>();
                for (ListIterator<E> l : iters) { //verifier que chaque iterateur nest pas nul
                        if (l == null) {
                                throw new NullPointerException();
                        }
                        fusion.add(l); 
                }

                lastCalledIter = -1;
                lastCalled = 0;
        }

        /**
         * Renvoie le comparateur selon lequel les éléments de cet itérateur sont
         * ordonnés.
         *
         * @return le comparateur selon lequel les éléments de cet itérateur sont
         * ordonnés
         * @ensures \result != null;
         * @pure
         */
        public Comparator<? super E> comparator() {
                return cmp;
        }

        /**
         * Renvoie true s'il reste un élément après dans l'itération.
         *
         * @return true s'il reste un élément après dans l'itération; false sinon
         * @ensures !\result <==> nextIndex() == iterModel.size();
         * @pure
         */
        @Override
        public boolean hasNext() {
                for (ListIterator<E> l : fusion) {
                 if (l.hasNext()) {
                 return true;
                        }
                }
        return false;
        }

        /**
         * Renvoie l'élèment qui sera renvoyé par le prochain appel à next().
         *
         * @return l'élèment qui sera renvoyé par le prochain appel à next()
         * @throws NoSuchElementException si l'itérateur n'a pas d'élément suivant
         * @requires hasNext();
         * @ensures \result.equals(iterModel.get(nextIndex()));
         * @pure
         */
        public E getNext() {
                 // Vérifie s'il existe un élément suivant
                if (!hasNext()) {
                        throw new NoSuchElementException();
                }
                // Sauvegarde les valeurs actuelles des indicateurs
                int tmp = lastCalled;
                int tmp2 = lastCalledIter;

                E res = next(); // Avance pour recuperer leeelément suivant
                previous();//reviens

                lastCalled = tmp; //restaure
                lastCalledIter = tmp2;

                return res;

        }

        /**
         * Renvoie l'élément suivant et avance le curseur.
         *
         * @return l'élément suivant
         * @throws NoSuchElementException si l'itérateur n'a pas d'élément suivant
         * @requires hasNext();
         * @ensures \result != null;
         * @ensures \result.equals(\old(getNext()));
         * @ensures \result.equals(getPrevious());
         * @ensures \result.equals(iterModel.get(previousIndex()))
         * @ensures \old(hasPrevious()) ==> comparator().compare(\old(getPrevious()),
         * \result) <= 0;
         * @ensures hasNext() ==> comparator().compare(\result, getNext()) <= 0;
         * @ensures hasPrevious();
         * @ensures previousIndex() == \old(nextIndex());
         * @ensures nextIndex() == \old(nextIndex() + 1);
         * @ensures lastCalled() == \old(nextIndex());
         * @ensures lastCalled() == previousIndex();
         */
        @Override
        public E next() {
                //verifier
                if (!hasNext()) {
                        throw new NoSuchElementException("Aucun élément suivant.");
                }

                E minElem = null; //stocker lelem minimum
                int minItIndex = -1;

                // Parcourt tous les itérateurs de 'fusion'
                for (int i = 0; i < fusion.size(); i++) {
                ListIterator<E> iter = fusion.get(i);
                 if (iter.hasNext()) {
                   E candidate = iter.next(); //lelement suivant
                        if (minElem == null || cmp.compare(candidate, minElem) < 0) { 
                         //annule le deplacement
                          if (minItIndex != -1) {
                             fusion.get(minItIndex).previous(); 
                                        }
                                //maj le min et indice
                                minElem = candidate;
                                minItIndex = i;
                                } else {
                                //annule le deplacement
                                 iter.previous(); 
                                }
                        }
                }
                //maj
                lastCalled = 1;
                lastCalledIter = minItIndex;

                //elem min
                return minElem;

        }

        /**
         * Renvoie true s'il y a un élément précédent dans l'itération.
         *
         * @return true s'il y a un élément précédent dans l'itération; false sinon
         * @ensures !\result <==> previousIndex() == -1;
         * @pure
         */
        @Override
        public boolean hasPrevious() {
                for (ListIterator<E> l : fusion) {
                  if (l.hasPrevious()) {
                        return true;
                  }
                }
           return false;
        }

        /**
         * Renvoie l'élèment qui sera renvoyé par le prochain appel à previous().
         *
         * @return l'élèment qui sera renvoyé par le prochain appel à previous()
         * @throws NoSuchElementException si l'itérateur n'a pas d'élément précédent
         * @requires hasPrevious();
         * @ensures \result.equals(iterModel.get(previousIndex()));
         * @pure
         */
        public E getPrevious() {
                if (!hasPrevious()) {
                        throw new NoSuchElementException();
                }
                int tmp = lastCalled;
                int tmp2 = lastCalledIter;

                E res = previous();
                next();

                lastCalled = tmp;
                lastCalledIter = tmp2;

                return res;

        }

        /**
         * Renvoie l'élément précédent et recule le curseur.
         *
         * @return l'élément précédent
         * @throws NoSuchElementException si l'itérateur n'a pas d'élément précédent
         * @requires hasPrevious();
         * @ensures hasNext();
         * @ensures \result != null;
         * @ensures \result.equals(\old(getPrevious()));
         * @ensures \result.equals(getNext());
         * @ensures \result.equals(\old(iterModel.get(previousIndex())));
         * @ensures \result.equals(iterModel.get(nextIndex()));
         * @ensures \old(hasNext()) ==> comparator().compare(\result,
         * iterModel.get(\old(nextIndex())) <= 0;
         * @ensures previousIndex() == \old(previousIndex()) - 1;
         * @ensures nextIndex() == \old(previousIndex());
         * @ensures lastCalled() == \old(previousIndex());
         * @ensures lastCalled() == nextIndex();
         */
        @Override
        public E previous() {
                if (!hasPrevious()) { //verif
                        throw new NoSuchElementException("Aucun élément précédent.");
                }
                E maxElem = null; //stocker max
                int maxIterIndex = -1;
                for (int i = 0; i < fusion.size(); i++) { //meme shema que precedente
                        ListIterator<E> iter = fusion.get(i);
                        if (iter.hasPrevious()) {
                                E candidate = iter.previous();
                                if (maxElem == null || cmp.compare(candidate, maxElem) > 0) {
                                        if (maxIterIndex != -1) {
                                                fusion.get(maxIterIndex).next(); // Annuler le déplacement pour l'ancien maximum
                                        }
                                        maxElem = candidate;
                                        maxIterIndex = i;
                                } else {
                                        // Annuler
                                        iter.next(); 
                                }
                        }
                }

                lastCalled = -1;
                lastCalledIter = maxIterIndex;
                return maxElem;
        }

        /**
         * Renvoie l'index de l'élément suivant dans l'itération. Renvoie le nombre
         * total d'élément dans l'itération s'il n'y a pas d'élément suivant.
         *
         * @return l'index de l'élément suivant dans l'itération
         * @ensures hasNext() <==> \result >= 0 && \result < iterModel.size();
         * @ensures !hasNext() <==> \result == iterModel.size();
         * @pure
         */
        @Override
        public int nextIndex() {
                int res = 0;
                //calcule la somme des indices nextIndex de tous les iter
                for (ListIterator<E> l : fusion) {
                        res += l.nextIndex();
                }
                return res;
        }

        /**
         * Renvoie l'index de l'élément précédent dans l'itération. Renvoie -1 s'il n'y
         * a pas d'élément précédent.
         *
         * @return l'index de l'élément précédent dans l'itération
         * @ensures hasPrevious() ==> \result >= 0;
         * @ensures !hasPrevious() <==> \result == -1;
         * @pure
         */
        @Override
        public int previousIndex() {
                return nextIndex() - 1;
        }

        /**
         * Renvoie l'index de l'élément renvoyé par le dernier appel à next() ou
         * previous(). Renvoie -1 si next() ou previous() n'ont jamais été appelés
         * depuis la création de cet itérateur ou bien si remove a été appelée depuis le
         * dernier appel à next ou previous.
         *
         * @return l'index de l'élément renvoyé par le dernier appel à next() ou
         * previous()
         * @ensures \result >= -1 && \result < iterModel.size();
         * @pure
         */
        public int lastIndex() {
                if (lastCalled == 1) {
                        return previousIndex();
                } else if (lastCalled == -1) {
                        return nextIndex();
                }
                return -1;
        }

        /**
         * Retire de l'itération le dernier élèment renvoyé par next() ou previous().
         * L'élément retiré est l'élèment renvoyé par le dernier appel à next() ou
         * previous(). Ne peut être appelé qu'une fois par appel à next ou previous.
         *
         * @throws IllegalStateException si next ou previous n'ont jamais été appelés,
         *                               ou bien si remove a déjà été appelé depuis le
         *                               dernier appel a next ou remove
         * @requires lastCalled() != -1;
         * @ensures iterModel.size() == \old(iterModel.size()) - 1;
         * @ensures (* if last move is previous : *) <br />
         * \old(lastCalled() == nextIndex()) ==> (previousIndex() ==
         * \old(previousIndex()));
         * @ensures (* if last move is next : *) <br />
         * \old(lastCalled() == previousIndex()) ==> (previousIndex() ==
         * \old(previousIndex()) - 1);
         * @ensures lastCalled() == -1;
         */
        @Override
        public void remove() {
                if (lastCalledIter == -1  || lastCalled == 0 ) {
                        throw new IllegalStateException();
                }
                // Supprime lelément du dernier iter
                fusion.get(lastCalledIter).remove();
                lastCalled = 0;
                lastCalledIter = -1;
        }

        /**
         * Opération non supportée.
         *
         * @throws UnsupportedOperationException toujours
         */
        @Override
        public void set(E e) {
                throw new UnsupportedOperationException();
        }

        /**
         * Opération non supportée.
         *
         * @throws UnsupportedOperationException toujours
         */
        @Override
        public void add(E e) {
                throw new UnsupportedOperationException();
        }

}


