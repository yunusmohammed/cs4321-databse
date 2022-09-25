SELECT * FROM Sailors;
SELECT Sailors.A FROM Sailors;
SELECT S.A FROM Sailors S;
SELECT * FROM Sailors S WHERE S.A < 3;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT * FROM Sailors S1, Sailors S2 WHERE S1.A < S2.A;
SELECT DISTINCT R.G FROM Reserves R;
SELECT * FROM Sailors ORDER BY Sailors.B;
SELECT Sailors.B, Sailors.A FROM Sailors WHERE Sailors.B = 100;
SELECT Boats.F, Boats.E, Boats.D FROM Boats WHERE 1 = 1;
SELECT * FROM Boats WHERE 1 = 2;
SELECT Reserves.H, Sailors.C FROM Reserves, Sailors, Boats WHERE Reserves.G = Sailors.A AND Boats.E = Sailors.A;
SELECT * FROM Boats2, Reserves2, Sailors2;
SELECT DISTINCT R.H, S.C FROM Reserves R, Sailors S, Boats B WHERE R.G = S.A AND B.E = S.A ORDER BY R.H, S.C;
SELECT DISTINCT * FROM Sailors S1, Sailors S2, Sailors S3 WHERE S1.B = S2.B AND S2.B = S3.B;
SELECT R.G, R.H FROM Reserves R WHERE R.G < 4 ORDER BY R.H;
SELECT DISTINCT * FROM Boats B1, Boats B2 WHERE 1=1 ORDER BY B1.F;
SELECT * FROM Reserves R ORDER BY R.G;
SELECT B.E, B.F, B.D, S.C FROM Boats B, Sailors S WHERE 1=1 ORDER BY S.C, B.F;
SELECT DISTINCT * FROM Reserves R1, Reserves R2, Reserves R3 WHERE R1.G = R2.G AND R2.G = R3.G AND R3.G = R1.G ORDER BY R3.G, R2.H;
SELECT DISTINCT R3.G, R1.H, R2.H FROM Reserves R1, Reserves R2, Reserves R3 WHERE R1.G = R2.G AND R2.G = R3.G AND R3.G = R1.G ORDER BY R3.G, R2.H;
SELECT DISTINCT S1.B, S2.C FROM Sailors S2, Sailors S1 WHERE S2.B > 301 AND S1.A < -1 ORDER BY S2.C;
SELECT * FROM Boats WHERE Boats.E > -1;
SELECT DISTINCT * FROM Sailors S1;
SELECT * FROM Reserves ORDER BY Reserves.H;
SELECT Sailors.C FROM Sailors, Reserves WHERE Sailors.A != Reserves.G;
SELECT DISTINCT * FROM Boats B1 ORDER BY B1.F;
SELECT * FROM Boats, Reserves WHERE Boats.D >= Reserves.H AND Boats.D <= Reserves.H;
SELECT Reserves.G FROM Reserves ORDER BY Reserves.G;
SELECT DISTINCT * FROM Reserves R WHERE R.G = R.G AND R.G < R.H ORDER BY R.H;
SELECT * FROM Sailors, Ships;



