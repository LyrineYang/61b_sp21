public class ClassPractice
{
    public static void main(String[] args)
    {
        int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
        int N = 10;
        drawTriangle(N);
        System.out.println(max(numbers));
        int[] a = {1, 2, -3, 4, 5, 4};
        int n = 3;
        windowPosSum(a, n);
        System.out.println(java.util.Arrays.toString(a));

    }
    public static int max(int[] m)
    {
        int max = 0;
        for (int member: m)
        {
            if (member > max)
            {
                max = member;
            }
        }
        return max;
    }
    public static void windowPosSum(int[] a, int n)
    {
        for (int i = 0; i < a.length; i++)
        {
            int sum = 0;
            if (a[i] < 0)
            {
                continue;
            }
            for(int j = 0; j <= n; j++)
            {
                if (i + j >= a.length )
                {
                    break;
                }
                sum += a[i+j];
            }
            a[i] = sum;
        }
    }

    public static void drawTriangle(int N) {
        int i = 0, j = 0;
        while (i < N)
        {
            j = 0;
            while (j <= i)
            {
                System.out.print('*');
                j++;
            }
            i++;
            System.out.println();
        }
    }
}




