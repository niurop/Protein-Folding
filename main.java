public class MyClass {
    public static void main(String args[]) {
        Folding.Fold(new Float(args[0]), new Float(args[1]), args[2]);
    }
}

class Folding{
    // static is faster + final is even faster (but constant)
    static final int origin = (1 << 31) + (1 << 15),
                     eX = 1, eY = 1 << 16;
    static boolean[] Hs;
    static int[] Ps, Vs, States, Mins, Sums, Ns, BestPs;
    static int n, index, Hnb;
    static void Fold(float P1, float P2, String code){
        n = code.length();
        Ps     = new int[n]; // curent points
        Vs     = new int[n]; // curent values
        States = new int[n]; // done paths, 1:R, 2:RL, 3:RLU, 4:RLUD
        Mins   = new int[n]; // alltime minimals, we know that the best will have the last one
        Sums   = new int[n]; // total sums
        Ns     = new int[n]; // total counts
        BestPs = new int[n]; // positions of best
        Hs = new boolean[n]; // for quick tests
        if(n == 0) return;
        for(int i = 0; i < n; i++){
            Hs[i] = code.charAt(i) == 'h' || code.charAt(i) == 'H'; // there should be a better way...
            if(Hs[i])Hnb++;
        }
        Ps[0] = origin; // start at origin
        if(n == 1) return;
        Ps[1] = origin + eX; // move right
        Vs[1] = Hs[0] && Hs[1] ? -1 : 0;
        if(n == 2) return;
        // we are done with 0 and 1
        States[0] = 4;
        States[1] = 4;
        index = 2;
        while(index > 1){ // we will be back only if all path are searched
            int P = Ps[index-1];
            switch(States[index]){ // decide on next direction
                case 0: Ps[index] = P + eX; break; // fst move right
                case 1: Ps[index] = P - eX; break; // snd move left
                case 2: Ps[index] = P + eY; break; // thd move up
                case 3: Ps[index] = P - eY; break; // fth move down
                case 4: index--; continue; // used all branches, go back
            }
            States[index]++; // remember that we moved
            int i;
            for(i = index; --i >= 0 && Ps[index] != Ps[i];); // if curent pos is taken, tray another
            if(i > -1) continue;
            Vs[index] = Vs[index-1]; // we can never get worst
            P = Ps[index];
            if(Hs[index]){ // calculate current value
                int R = P + eX, L = P - eX, U = P + eY, D = P - eY, total = 0;
                for(i = index; --i >= 0 ;)
                    if(Ps[i] == R || Ps[i] == L || Ps[i] == U || Ps[i] == D){
                        if(Hs[i]) Vs[index]--;
                        if(++total == 4) break;
                    }
            }
            Sums[index] += Vs[index];
            Ns[index]++;
            boolean newMin = Vs[index] < Mins[index];
            double rand = Math.random();
            if(Vs[index] <= Mins[index]) Mins[index] = Vs[index]; // new minimum, definitly keap
            else if((Vs[index] > Sums[index] / Ns[index] && rand > P2) || (Vs[index] <= Sums[index] / Ns[index] && rand > P1))
                continue; // well, not good enogth
            
            if(index == n-1){ // we are in the farest node, so lets update our best
                if(newMin) for(i = 0; i < n; i++) BestPs[i] = Ps[i];
                continue;
            }
            index++; // go up
            States[index] = 0; // reset path already taken
        }
        System.out.println("Length: " + Integer.toString(n));
        System.out.println("With #H: " + Integer.toString(Hnb));
        System.out.println("Best value found: " + Integer.toString(Mins[n-1]));
        for(int i = 0; i < n; i++)
            System.out.println((Hs[i] ? "H" : "P") + "-(" + Integer.toString((BestPs[i] & 0xFFFF) - 0x8000) + ", " + Integer.toString(((BestPs[i] >> 16) & 0xFFFF) - 0x8000) + ")");
        
    }
}
