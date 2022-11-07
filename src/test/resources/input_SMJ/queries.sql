SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G AND Sailors.A = Reserves.H;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 50;
SELECT * FROM Sailors S1, Sailors S2 WHERE S1.A = S2.A AND S2.A > 5995;