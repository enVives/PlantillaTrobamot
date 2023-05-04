package com.example.plantillatrobamot;

import java.util.Iterator;

public class UnsortedArrayMapping<K, V>
{
    // Atributos:
    private K[] keys;
    private V[] values;
    private int n;

    // Reserva memòria pels dos arrays de longitud max i inicialitza n a 0
    public UnsortedArrayMapping(int max)
    {
        keys = (K[]) new Object[max];
        values = (V[]) new Object[max];
        n = 0;
    }

    // O(n): cerca lineal
    // Consultar el valor associat a la clau
    public V get(K key)
    {
        int i = -1;
        boolean trobat = false;
        while (!trobat && i < n - 1) {
            i++;
            trobat = key.equals(keys[i]);
        }
        if (trobat) {
            return values[i];
        } else {
            return null;
        }
    }

    // O(n): Retorna el valor anterior associat a la clau (cerca lineal), si n’hi havia
    // Afegir una parella clau-valor
    // Retorna el valor anterior associat a la clau, si n’hi havia, o null
    public V put(K key, V value)
    {
        int i = 0;
        V valor_anterior;
        for(i = 0; i < n; i++){
            if(keys[i].equals(key)){
                valor_anterior = values[i];
                values[i] = value;
                return valor_anterior;
            }
        }
        keys[n] = key;
        values[n] = value;
        n++;

        return null;
    }

    // O(n): Retorna el valor anterior associat a la clau (cerca lineal), si n’hi havia
    // Eliminar l’associació d’una clau
    // Retorna el valor associat a la clau, si n’hi havia, o null
    public V remove(K key)
    {
        boolean found = false;
        V valorAnterior;

        for (int i = 0; !found && i < n; i++)
        {
            if (key.equals(keys[i]))
            {
                valorAnterior = values[i];
                values[i] = null;
                return valorAnterior;
            }
        }
        return null;
    }

    // O(1)
    public boolean isEmpty()
    {
        return (n == 0);
    }

    protected  class Pair{
        private K key;
        private V value;
        private Pair(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
    private class IteratorUnsortedArrayMapping implements Iterator {
        private int IdxIterator;

        private IteratorUnsortedArrayMapping() {
            IdxIterator = 0;
        }

        @Override
        public boolean hasNext() {
            return IdxIterator < n;
        }

        @Override
        public Object next() {
            Pair p = new Pair(keys[IdxIterator],values[IdxIterator]);
            IdxIterator++;
            return p;
        }

    }

    public Iterator iterator() {
        Iterator it = new IteratorUnsortedArrayMapping();
        return it;
    }
}

