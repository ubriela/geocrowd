
for i = 0 : 20
    size=1000
    X=zeros(size,2);
    for j = 1 : size
       X(j,1) = 5*rand;
       X(j,2) = 5*rand;
    end
    plot(X(:,1),X(:,2), 'r.')
    
    file = strcat('uni-tasks',int2str(i),'.txt')
    dlmwrite(file, X)
end