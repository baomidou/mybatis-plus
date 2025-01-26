/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.parser.cache;

import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;

/**
 * Fury Factory.
 *
 * @author laokou
 */
public final class FuryFactory {

    private static final FuryFactory FACTORY = new FuryFactory();

    private final ThreadSafeFury FURY = Fury.builder()
        // 开启异步编译
        .withAsyncCompilation(true)
        .buildThreadSafeFury();

    public FuryFactory() {
        FURY.register(net.sf.jsqlparser.expression.Alias.class);
        FURY.register(net.sf.jsqlparser.expression.Alias.AliasColumn.class);
        FURY.register(net.sf.jsqlparser.expression.AllValue.class);
        FURY.register(net.sf.jsqlparser.expression.AnalyticExpression.class);
        FURY.register(net.sf.jsqlparser.expression.AnyComparisonExpression.class);
        FURY.register(net.sf.jsqlparser.expression.ArrayConstructor.class);
        FURY.register(net.sf.jsqlparser.expression.ArrayExpression.class);
        FURY.register(net.sf.jsqlparser.expression.CaseExpression.class);
        FURY.register(net.sf.jsqlparser.expression.CastExpression.class);
        FURY.register(net.sf.jsqlparser.expression.CollateExpression.class);
        FURY.register(net.sf.jsqlparser.expression.ConnectByRootOperator.class);
        FURY.register(net.sf.jsqlparser.expression.DateTimeLiteralExpression.class);
        FURY.register(net.sf.jsqlparser.expression.DateValue.class);
        FURY.register(net.sf.jsqlparser.expression.DoubleValue.class);
        FURY.register(net.sf.jsqlparser.expression.ExtractExpression.class);
        FURY.register(net.sf.jsqlparser.expression.FilterOverImpl.class);
        FURY.register(net.sf.jsqlparser.expression.Function.class);
        FURY.register(net.sf.jsqlparser.expression.HexValue.class);
        FURY.register(net.sf.jsqlparser.expression.IntervalExpression.class);
        FURY.register(net.sf.jsqlparser.expression.JdbcNamedParameter.class);
        FURY.register(net.sf.jsqlparser.expression.JdbcParameter.class);
        FURY.register(net.sf.jsqlparser.expression.JsonAggregateFunction.class);
        FURY.register(net.sf.jsqlparser.expression.JsonExpression.class);
        FURY.register(net.sf.jsqlparser.expression.JsonFunction.class);
        FURY.register(net.sf.jsqlparser.expression.JsonFunctionExpression.class);
        FURY.register(net.sf.jsqlparser.expression.JsonKeyValuePair.class);
        FURY.register(net.sf.jsqlparser.expression.KeepExpression.class);
        FURY.register(net.sf.jsqlparser.expression.LongValue.class);
        FURY.register(net.sf.jsqlparser.expression.MySQLGroupConcat.class);
        FURY.register(net.sf.jsqlparser.expression.MySQLIndexHint.class);
        FURY.register(net.sf.jsqlparser.expression.NextValExpression.class);
        FURY.register(net.sf.jsqlparser.expression.NotExpression.class);
        FURY.register(net.sf.jsqlparser.expression.NullValue.class);
        FURY.register(net.sf.jsqlparser.expression.NumericBind.class);
        FURY.register(net.sf.jsqlparser.expression.OracleHierarchicalExpression.class);
        FURY.register(net.sf.jsqlparser.expression.OracleHint.class);
        FURY.register(net.sf.jsqlparser.expression.OracleNamedFunctionParameter.class);
        FURY.register(net.sf.jsqlparser.expression.OrderByClause.class);
        FURY.register(net.sf.jsqlparser.expression.OverlapsCondition.class);
        FURY.register(net.sf.jsqlparser.expression.PartitionByClause.class);
        FURY.register(net.sf.jsqlparser.expression.RangeExpression.class);
        FURY.register(net.sf.jsqlparser.expression.RowConstructor.class);
        FURY.register(net.sf.jsqlparser.expression.RowGetExpression.class);
        FURY.register(net.sf.jsqlparser.expression.SQLServerHints.class);
        FURY.register(net.sf.jsqlparser.expression.SignedExpression.class);
        FURY.register(net.sf.jsqlparser.expression.StringValue.class);
        FURY.register(net.sf.jsqlparser.expression.TimeKeyExpression.class);
        FURY.register(net.sf.jsqlparser.expression.TimeValue.class);
        FURY.register(net.sf.jsqlparser.expression.TimestampValue.class);
        FURY.register(net.sf.jsqlparser.expression.TimezoneExpression.class);
        FURY.register(net.sf.jsqlparser.expression.TranscodingFunction.class);
        FURY.register(net.sf.jsqlparser.expression.TrimFunction.class);
        FURY.register(net.sf.jsqlparser.expression.UserVariable.class);
        FURY.register(net.sf.jsqlparser.expression.VariableAssignment.class);
        FURY.register(net.sf.jsqlparser.expression.WhenClause.class);
        FURY.register(net.sf.jsqlparser.expression.WindowDefinition.class);
        FURY.register(net.sf.jsqlparser.expression.WindowElement.class);
        FURY.register(net.sf.jsqlparser.expression.WindowOffset.class);
        FURY.register(net.sf.jsqlparser.expression.WindowRange.class);
        FURY.register(net.sf.jsqlparser.expression.XMLSerializeExpr.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.Addition.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.Concat.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.Division.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.Modulo.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.Multiplication.class);
        FURY.register(net.sf.jsqlparser.expression.operators.arithmetic.Subtraction.class);
        FURY.register(net.sf.jsqlparser.expression.operators.conditional.AndExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.conditional.OrExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.conditional.XorExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.Between.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.ContainedBy.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.Contains.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.DoubleAnd.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.EqualsTo.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.ExistsExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.ExpressionList.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.FullTextSearch.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.GeometryDistance.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.GreaterThan.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.InExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.IsDistinctExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.IsNullExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.JsonOperator.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.LikeExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.Matches.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.MemberOfExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.MinorThan.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.MinorThanEquals.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.NamedExpressionList.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.NotEqualsTo.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.SimilarToExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.TSQLLeftJoin.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.TSQLRightJoin.class);
        FURY.register(net.sf.jsqlparser.parser.ASTNodeAccessImpl.class);
        FURY.register(net.sf.jsqlparser.parser.Token.class);
        FURY.register(net.sf.jsqlparser.schema.Column.class);
        FURY.register(net.sf.jsqlparser.schema.Sequence.class);
        FURY.register(net.sf.jsqlparser.schema.Synonym.class);
        FURY.register(net.sf.jsqlparser.schema.Table.class);
        FURY.register(net.sf.jsqlparser.statement.Block.class);
        FURY.register(net.sf.jsqlparser.statement.Commit.class);
        FURY.register(net.sf.jsqlparser.statement.DeclareStatement.class);
        FURY.register(net.sf.jsqlparser.statement.DeclareStatement.TypeDefExpr.class);
        FURY.register(net.sf.jsqlparser.statement.DescribeStatement.class);
        FURY.register(net.sf.jsqlparser.statement.ExplainStatement.class);
        FURY.register(net.sf.jsqlparser.statement.ExplainStatement.Option.class);
        FURY.register(net.sf.jsqlparser.statement.IfElseStatement.class);
        FURY.register(net.sf.jsqlparser.statement.OutputClause.class);
        FURY.register(net.sf.jsqlparser.statement.PurgeStatement.class);
        FURY.register(net.sf.jsqlparser.statement.ReferentialAction.class);
        FURY.register(net.sf.jsqlparser.statement.ResetStatement.class);
        FURY.register(net.sf.jsqlparser.statement.RollbackStatement.class);
        FURY.register(net.sf.jsqlparser.statement.SavepointStatement.class);
        FURY.register(net.sf.jsqlparser.statement.SetStatement.class);
        FURY.register(net.sf.jsqlparser.statement.ShowColumnsStatement.class);
        FURY.register(net.sf.jsqlparser.statement.ShowStatement.class);
        FURY.register(net.sf.jsqlparser.statement.Statements.class);
        FURY.register(net.sf.jsqlparser.statement.UnsupportedStatement.class);
        FURY.register(net.sf.jsqlparser.statement.UseStatement.class);
        FURY.register(net.sf.jsqlparser.statement.alter.Alter.class);
        FURY.register(net.sf.jsqlparser.statement.alter.AlterExpression.class);
        FURY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDataType.class);
        FURY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDropDefault.class);
        FURY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDropNotNull.class);
        FURY.register(net.sf.jsqlparser.statement.alter.AlterSession.class);
        FURY.register(net.sf.jsqlparser.statement.alter.AlterSystemStatement.class);
        FURY.register(net.sf.jsqlparser.statement.alter.RenameTableStatement.class);
        FURY.register(net.sf.jsqlparser.statement.alter.sequence.AlterSequence.class);
        FURY.register(net.sf.jsqlparser.statement.analyze.Analyze.class);
        FURY.register(net.sf.jsqlparser.statement.comment.Comment.class);
        FURY.register(net.sf.jsqlparser.statement.create.function.CreateFunction.class);
        FURY.register(net.sf.jsqlparser.statement.create.index.CreateIndex.class);
        FURY.register(net.sf.jsqlparser.statement.create.procedure.CreateProcedure.class);
        FURY.register(net.sf.jsqlparser.statement.create.schema.CreateSchema.class);
        FURY.register(net.sf.jsqlparser.statement.create.sequence.CreateSequence.class);
        FURY.register(net.sf.jsqlparser.statement.create.synonym.CreateSynonym.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.CheckConstraint.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.ColDataType.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.ColumnDefinition.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.CreateTable.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.ExcludeConstraint.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.ForeignKeyIndex.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.Index.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.Index.ColumnParams.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.NamedConstraint.class);
        FURY.register(net.sf.jsqlparser.statement.create.table.RowMovement.class);
        FURY.register(net.sf.jsqlparser.statement.create.view.AlterView.class);
        FURY.register(net.sf.jsqlparser.statement.create.view.CreateView.class);
        FURY.register(net.sf.jsqlparser.statement.delete.Delete.class);
        FURY.register(net.sf.jsqlparser.statement.drop.Drop.class);
        FURY.register(net.sf.jsqlparser.statement.execute.Execute.class);
        FURY.register(net.sf.jsqlparser.statement.grant.Grant.class);
        FURY.register(net.sf.jsqlparser.statement.insert.Insert.class);
        FURY.register(net.sf.jsqlparser.statement.insert.InsertConflictAction.class);
        FURY.register(net.sf.jsqlparser.statement.insert.InsertConflictTarget.class);
        FURY.register(net.sf.jsqlparser.statement.merge.Merge.class);
        FURY.register(net.sf.jsqlparser.statement.merge.MergeDelete.class);
        FURY.register(net.sf.jsqlparser.statement.merge.MergeInsert.class);
        FURY.register(net.sf.jsqlparser.statement.merge.MergeUpdate.class);
        FURY.register(net.sf.jsqlparser.statement.refresh.RefreshMaterializedViewStatement.class);
        FURY.register(net.sf.jsqlparser.statement.select.AllColumns.class);
        FURY.register(net.sf.jsqlparser.statement.select.AllTableColumns.class);
        FURY.register(net.sf.jsqlparser.statement.select.Distinct.class);
        FURY.register(net.sf.jsqlparser.statement.select.ExceptOp.class);
        FURY.register(net.sf.jsqlparser.statement.select.Fetch.class);
        FURY.register(net.sf.jsqlparser.statement.select.First.class);
        FURY.register(net.sf.jsqlparser.statement.select.ForClause.class);
        FURY.register(net.sf.jsqlparser.statement.select.GroupByElement.class);
        FURY.register(net.sf.jsqlparser.statement.select.IntersectOp.class);
        FURY.register(net.sf.jsqlparser.statement.select.Join.class);
        FURY.register(net.sf.jsqlparser.statement.select.KSQLJoinWindow.class);
        FURY.register(net.sf.jsqlparser.statement.select.KSQLWindow.class);
        FURY.register(net.sf.jsqlparser.statement.select.LateralSubSelect.class);
        FURY.register(net.sf.jsqlparser.statement.select.LateralView.class);
        FURY.register(net.sf.jsqlparser.statement.select.Limit.class);
        FURY.register(net.sf.jsqlparser.statement.select.MinusOp.class);
        FURY.register(net.sf.jsqlparser.statement.select.Offset.class);
        FURY.register(net.sf.jsqlparser.statement.select.OptimizeFor.class);
        FURY.register(net.sf.jsqlparser.statement.select.OrderByElement.class);
        FURY.register(net.sf.jsqlparser.statement.select.ParenthesedFromItem.class);
        FURY.register(net.sf.jsqlparser.statement.select.ParenthesedSelect.class);
        FURY.register(net.sf.jsqlparser.statement.select.Pivot.class);
        FURY.register(net.sf.jsqlparser.statement.select.PivotXml.class);
        FURY.register(net.sf.jsqlparser.statement.select.PlainSelect.class);
        FURY.register(net.sf.jsqlparser.statement.select.SelectItem.class);
        FURY.register(net.sf.jsqlparser.statement.select.SetOperationList.class);
        FURY.register(net.sf.jsqlparser.statement.select.Skip.class);
        FURY.register(net.sf.jsqlparser.statement.select.TableFunction.class);
        FURY.register(net.sf.jsqlparser.statement.select.TableStatement.class);
        FURY.register(net.sf.jsqlparser.statement.select.Top.class);
        FURY.register(net.sf.jsqlparser.statement.select.UnPivot.class);
        FURY.register(net.sf.jsqlparser.statement.select.UnionOp.class);
        FURY.register(net.sf.jsqlparser.statement.select.Values.class);
        FURY.register(net.sf.jsqlparser.statement.select.Wait.class);
        FURY.register(net.sf.jsqlparser.statement.select.WithIsolation.class);
        FURY.register(net.sf.jsqlparser.statement.select.WithItem.class);
        FURY.register(net.sf.jsqlparser.statement.show.ShowIndexStatement.class);
        FURY.register(net.sf.jsqlparser.statement.show.ShowTablesStatement.class);
        FURY.register(net.sf.jsqlparser.statement.truncate.Truncate.class);
        FURY.register(net.sf.jsqlparser.statement.update.Update.class);
        FURY.register(net.sf.jsqlparser.statement.update.UpdateSet.class);
        FURY.register(net.sf.jsqlparser.statement.upsert.Upsert.class);
        FURY.register(net.sf.jsqlparser.util.cnfexpression.MultiAndExpression.class);
        FURY.register(net.sf.jsqlparser.util.cnfexpression.MultiOrExpression.class);
        FURY.register(net.sf.jsqlparser.expression.BinaryExpression.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.ComparisonOperator.class);
        FURY.register(net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression.class);
        FURY.register(net.sf.jsqlparser.expression.Function.NullHandling.class);
        FURY.register(net.sf.jsqlparser.statement.CreateFunctionalStatement.class);
        FURY.register(net.sf.jsqlparser.statement.select.Select.class);
        FURY.register(net.sf.jsqlparser.statement.select.SetOperation.class);
        FURY.register(net.sf.jsqlparser.util.cnfexpression.MultipleExpression.class);
        FURY.register(net.sf.jsqlparser.statement.insert.InsertModifierPriority.class);
        FURY.register(net.sf.jsqlparser.statement.select.OrderByElement.NullOrdering.class);
        FURY.register(net.sf.jsqlparser.statement.select.ForMode.class);
        FURY.register(net.sf.jsqlparser.statement.select.MySqlSqlCacheFlags.class);
        FURY.register(net.sf.jsqlparser.statement.select.PlainSelect.BigQuerySelectQualifier.class);
        FURY.register(net.sf.jsqlparser.statement.update.UpdateModifierPriority.class);
    }

    public static FuryFactory getFuryFactory() {
        return FACTORY;
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        }
        return FURY.serialize(object);
    }

    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return FURY.deserialize(bytes);
    }

}
