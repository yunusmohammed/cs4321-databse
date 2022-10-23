SELECT Ghana.A FROM Ghana, Kenya WHERE Ghana.A = Kenya.B;
SELECT DISTINCT * FROM Ghana G WHERE G.A = G.B AND G.B = G.C ORDER BY G.B;
SELECT Kenya.A FROM Kenya, Ghana, Eritrea WHERE Kenya.B = Ghana.A AND Eritrea.B = Ghana.A AND Ghana.B = Eritrea.A;
