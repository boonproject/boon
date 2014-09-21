<c:forEach items='${fn:sortBy(employees, firstName, lastName)}'>
    ${this.firstName}, ${this.lastName}
</c:forEach>