w=500
s=0.9*w;
e=round(s/4);
r=w-e*4
for i = 0 : 20
    x = randi([1, 4])
    y = randi([1, 4])
    MU1 = [x y];
    SIGMA1 = [.05 0; 0 .05];
    
    x = randi([1, 4])
    y = randi([1, 4])
    MU2 = [x y];
    SIGMA2 = [.05 0; 0 .05];
    
    x = randi([1, 4])
    y = randi([1, 4])
    MU3 = [x y];
    SIGMA3 = [.05 0; 0 .05];
    
    x = randi([1, 4])
    y = randi([1, 4])
    MU4 = [x y];
    SIGMA4 = [.05 0; 0 .05];
    
    Y=zeros(r,2);
    for j = 1 : r
       Y(j,1) = 5*rand;
       Y(j,2) = 5*rand;
    end
    
    X = [mvnrnd(MU1,SIGMA1,e);mvnrnd(MU2,SIGMA2,e);mvnrnd(MU3,SIGMA3,e);mvnrnd(MU4,SIGMA4,e)];
    X = cat(1,X,Y);
    scatter(X(:,1),X(:,2),'.')
    file = strcat('sync-workers',int2str(i),'.txt')
    dlmwrite(file, X)
end